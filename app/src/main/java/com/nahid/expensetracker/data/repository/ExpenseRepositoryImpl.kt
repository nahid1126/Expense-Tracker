package com.nahid.expensetracker.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import com.google.firebase.firestore.Source
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.core.utils.extension.getSpecificException
import com.nahid.expensetracker.data.local.AppPreference
import com.nahid.expensetracker.data.local.dao.ExpenseDao
import java.io.IOException
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
import com.nahid.expensetracker.data.local.entity.toDomain
import com.nahid.expensetracker.data.model.ExpenseCategoryDto
import com.nahid.expensetracker.data.model.ExpenseDto
import com.nahid.expensetracker.data.model.ExpenseTypeDto
import com.nahid.expensetracker.data.model.toDto
import com.nahid.expensetracker.data.worker.SyncWorker
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.ExpenseCategory
import com.nahid.expensetracker.domain.model.ExpenseType
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class ExpenseRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val expenseDao: ExpenseDao,
    private val workManager: WorkManager,
    private val appPreference: AppPreference,
) : ExpenseRepository {
    private val cloudMutex = Mutex()

    override suspend fun getExpCategory(): Results<List<ExpenseCategory>> {
        val fallback = listOf("Food", "Transport", "Shopping", "Home", "Health", "Entertainment", "Salary", "Pocket Money", "Other")
            .mapIndexed { index, name -> ExpenseCategory(index.toLong() + 1, name) }
        val remote = loadOptions("expenseCategory") { it.toObject(ExpenseCategoryDto::class.java)?.toDomain() }
        val categories = remote.filter { it.name.isNotBlank() }.sortedBy { it.id }
        if (categories.isNotEmpty()) {
            try {
                appPreference.saveExpenseCategories(categories)
            } catch (_: IOException) {
                // The fetched categories are still usable for this session.
            }
            return Results.Success(categories)
        }
        val cached = try {
            appPreference.readExpenseCategories().first()
        } catch (_: IOException) {
            emptyList()
        }
        return Results.Success(cached.ifEmpty { fallback })
    }

    override suspend fun getExpenseType(): Results<List<ExpenseType>> {
        val defaultTypes = listOf(ExpenseType(1, "Expense"), ExpenseType(2, "Income"))
        val remote = loadOptions("expenseType") { it.toObject(ExpenseTypeDto::class.java)?.toDomain() }
        val types = defaultTypes.map { fallback -> remote.firstOrNull { it.name == fallback.name } ?: fallback }
        return Results.Success(types)
    }

    private suspend fun <T> loadOptions(
        collection: String,
        convert: (com.google.firebase.firestore.DocumentSnapshot) -> T?,
    ): List<T> {
        val reference = db.collection(collection)
        val result = withTimeoutOrNull(1_500) {
            when (val response = attempt { reference.get(Source.SERVER).await().documents.mapNotNull(convert) }) {
                is Results.Success -> response.data
                is Results.Error -> emptyList()
            }
        }
        return result.orEmpty()
    }

    override suspend fun insertExpense(expense: Expense): Results<Boolean> = withAccount { uid ->
        val remoteId = newId()
        expenseDao.insertExpense(expense.toOwnedEntity(uid, remoteId, isSynced = false))
        scheduleSync(uid)
        true
    }

    override suspend fun updateExpense(expense: Expense): Results<Boolean> = withAccount { uid ->
        val id = requireNotNull(expense.id)
        check(expenseDao.edit(id, uid, expense.title, expense.amount, expense.date,
            expense.type, expense.category, newId()) == 1) { "Transaction is no longer available" }
        scheduleSync(uid)
        true
    }

    override suspend fun deleteExpense(expense: Expense): Results<Boolean> = withAccount { uid ->
        check(expenseDao.markDeleted(requireNotNull(expense.id), uid, newId()) == 1) {
            "Transaction is no longer available"
        }
        scheduleSync(uid)
        true
    }

    override suspend fun fetchAndStoreExpenses(): Results<Unit> = withAccount { uid ->
        if (expenseDao.getPending(uid).isNotEmpty()) scheduleSync(uid)
        cloudMutex.withLock {
            val documents = expenses(uid).get(Source.SERVER).await().documents
            val rows = documents.mapNotNull { document ->
                document.toObject(ExpenseDto::class.java)?.toDomain()
                    ?.toOwnedEntity(uid, document.id, isSynced = true)
            }
            expenseDao.mergeSnapshot(uid, documents.mapTo(mutableSetOf()) { it.id }, rows)
        }
    }

    override suspend fun syncUnsyncedExpenses(uid: String): Results<Unit> = attempt {
        cloudMutex.withLock {
            var retryableError: FirebaseFirestoreException? = null
            for (entity in expenseDao.getPending(uid)) {
                if (auth.currentUser?.uid != uid) break
                val id = requireNotNull(entity.id)
                val reference = expenses(uid).document(entity.remoteId)
                try {
                    if (entity.isDeleted) {
                        reference.delete().await()
                        expenseDao.removeTombstone(id, uid, entity.revision)
                    } else {
                        reference.set(entity.toDomain().toDto()).await()
                        expenseDao.acknowledge(id, uid, entity.revision)
                    }
                } catch (error: FirebaseFirestoreException) {
                    if (auth.currentUser?.uid != uid) break
                    when (error.code) {
                        Code.PERMISSION_DENIED, Code.UNAUTHENTICATED, Code.INVALID_ARGUMENT,
                        Code.FAILED_PRECONDITION, Code.OUT_OF_RANGE, Code.UNIMPLEMENTED -> {
                            expenseDao.markSyncFailed(id, uid, entity.revision)
                        }
                        else -> retryableError = error
                    }
                }
            }
            retryableError?.let { throw it }
        }
    }

    private fun scheduleSync(uid: String) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(workDataOf(SyncWorker.OWNER_UID to uid))
            .build()
        workManager.enqueueUniqueWork("ExpenseSync:$uid", ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }

    override suspend fun retryFailedSync(): Results<Unit> = withAccount { uid ->
        expenseDao.resetFailedSync(uid)
        scheduleSync(uid)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeFailedSyncCount(): Flow<Int> = observeAccount().flatMapLatest { uid ->
        if (uid == null) flowOf(0) else expenseDao.observeFailedSyncCount(uid)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllExpense(): Flow<List<Expense>> = observeAccount().flatMapLatest { uid ->
        if (uid == null) flowOf(emptyList())
        else expenseDao.observeExpenses(uid).map { rows -> rows.map(ExpenseEntity::toDomain) }
    }

    private fun observeAccount(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    private fun expenses(uid: String) = db.collection("users").document(uid).collection("expenses")

    private suspend fun <T> withAccount(action: suspend (String) -> T): Results<T> = attempt {
        val uid = auth.currentUser?.uid
        if (uid == null) throw IllegalStateException("User not logged in")
        action(uid)
    }

    private suspend fun <T> attempt(action: suspend () -> T): Results<T> = try {
        Results.Success(action())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Results.Error(error.getSpecificException())
    }

    private fun Expense.toOwnedEntity(uid: String, remoteId: String, isSynced: Boolean) = ExpenseEntity(
        title = title,
        amount = amount,
        date = date,
        type = type,
        category = category,
        ownerUid = uid,
        remoteId = remoteId,
        isSynced = isSynced,
    )

    private fun newId() = UUID.randomUUID().toString()
}

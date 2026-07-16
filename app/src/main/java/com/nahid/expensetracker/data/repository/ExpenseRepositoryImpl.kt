package com.nahid.expensetracker.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.core.utils.NetworkHelper
import com.nahid.expensetracker.core.utils.exception.AppException
import com.nahid.expensetracker.core.utils.extension.getSpecificException
import com.nahid.expensetracker.data.local.dao.ExpenseDao
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
import com.nahid.expensetracker.data.local.entity.toDomain
import com.nahid.expensetracker.data.local.entity.toEntity
import com.nahid.expensetracker.data.model.ExpenseCategoryDto
import com.nahid.expensetracker.data.model.ExpenseTypeDto
import com.nahid.expensetracker.data.model.toDto
import com.nahid.expensetracker.data.worker.SyncWorker
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.ExpenseCategory
import com.nahid.expensetracker.domain.model.ExpenseType
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val TAG = "ExpenseRepositoryImpl"

class ExpenseRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val expenseDao: ExpenseDao,
    private val networkHelper: NetworkHelper,
    private val workManager: WorkManager,
) : ExpenseRepository {

    override suspend fun getExpCategory(): Results<List<ExpenseCategory>> {
        return try {
            val snap = db.collection("expenseCategory")
                .get()
                .await()

            val categories = snap.documents.mapNotNull { doc ->
                doc.toObject(ExpenseCategoryDto::class.java)
            }

            Results.Success(categories.map { it.toDomain() }.sortedBy { it.id })
        } catch (e: Exception) {
            Log.e(TAG, "getExpCategory: ${e.message}")
            Results.Error(e.getSpecificException())
        }
    }

    override suspend fun getExpenseType(): Results<List<ExpenseType>> {
        return try {
            val snap = db.collection("expenseType")
                .get()
                .await()

            val types = snap.documents.mapNotNull { doc ->
                doc.toObject(ExpenseTypeDto::class.java)
            }

            Results.Success(types.map { it.toDomain() }.sortedBy { it.id })
        } catch (e: Exception) {
            Log.e(TAG, "getExpenseType: ${e.message}")
            Results.Error(e.getSpecificException())
        }
    }


    override suspend fun insertExpense(expense: Expense): Results<Boolean> {
        return try {
            expenseDao.insertExpense(expense.toEntity())
            Results.Success(true)
        } catch (e: Exception) {
            Results.Error(e.getSpecificException())
        }
    }

    override suspend fun syncUnsyncedExpenses(): Results<Unit> {
        val currentUser = auth.currentUser
            ?: return Results.Error(AppException.AuthException("User not authenticated"))
        if (!networkHelper.isNetworkConnected()) return Results.Error(AppException.NetworkException())

        return try {
            val unsyncedExpenses = expenseDao.getUnsyncedExpenses()
            unsyncedExpenses.forEach { expense ->
                val expenseDto = expense.toDomain()
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("expenses")
                    .add(expenseDto)
                    .await()

                expenseDao.updateExpense(expense.copy(isSynced = true))
            }
            Results.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "syncUnsyncedExpenses error: ${e.message}")
            Results.Error(e.getSpecificException())
        }
    }

    override fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "ExpenseSyncWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    override fun getTopExpense(): Flow<List<Expense>> {
        return expenseDao.getAllTopExpense().map { list->
            list.map { it.toDomain() }
        }
    }
    override fun getLastFiveExpense(): Flow<List<Expense>> {
        return expenseDao.getLastFiveExpense().map { list->
            list.map { it.toDomain() }
        }
    }

    override fun getAllExpense(): Flow<List<Expense>> {
        return expenseDao.getAllExpense().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllFilterExpense(type: String): Flow<List<Expense>> {
        return expenseDao.getAllFilterExpense(type).map { list->
            list.map { it.toDomain() }
        }
    }
}
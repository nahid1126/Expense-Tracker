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
import com.nahid.expensetracker.data.local.entity.toDomain
import com.nahid.expensetracker.data.local.entity.toEntity
import com.nahid.expensetracker.data.model.ExpenseCategoryDto
import com.nahid.expensetracker.data.model.ExpenseDto
import com.nahid.expensetracker.data.model.ExpenseTypeDto
import com.nahid.expensetracker.data.model.toDto
import com.nahid.expensetracker.data.worker.SyncWorker
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.ExpenseCategory
import com.nahid.expensetracker.domain.model.ExpenseType
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
            // Offline First: Always insert into Room first
            expenseDao.insertExpense(expense.toEntity())
            // Schedule background sync
            scheduleSync()
            Results.Success(true)
        } catch (e: Exception) {
            Results.Error(e.getSpecificException())
        }
    }

    override suspend fun updateExpense(expense: Expense): Results<Boolean> {
        return try {
            // 1. Update in local database
            expenseDao.updateExpense(expense.toEntity())
            
            // 2. Update in Firebase (we can reuse the set logic)
            insertExpenseToFirebase(expense)
            
            Results.Success(true)
        } catch (e: Exception) {
            Results.Error(e.getSpecificException())
        }
    }


    override suspend fun deleteExpense(expense: Expense): Results<Boolean> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Results.Error(AppException.AuthException("User not logged in"))

            // 1. Delete from local database
            expense.id?.let { expenseDao.deleteExpenseById(it) }

            // 2. Delete from Firebase
            if (expense.id != null) {
                db.collection("users")
                    .document(uid)
                    .collection("expenses")
                    .document(expense.id.toString())
                    .delete()
                    .await()
            }
            
            Results.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "deleteExpense: ${e.message}")
            Results.Error(e.getSpecificException())
        }
    }

    override suspend fun insertExpenseToFirebase(expense: Expense): Results<Boolean> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Results.Error(AppException.AuthException("User not logged in"))
            
            val expenseDto = expense.toDto()
            val docRef = if (expense.id != null) {
                db.collection("users").document(uid).collection("expenses").document(expense.id.toString())
            } else {
                db.collection("users").document(uid).collection("expenses").document()
            }
            
            docRef.set(expenseDto).await()
            Results.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "insertExpenseToFirebase: ${e.message}")
            Results.Error(e.getSpecificException())
        }
    }

    override suspend fun fetchAndStoreExpenses(): Results<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Results.Error(AppException.AuthException("User not logged in"))
            
            val snap = db.collection("users")
                .document(uid)
                .collection("expenses")
                .get()
                .await()

            val expenses = snap.documents.mapNotNull { doc ->
                doc.toObject(ExpenseDto::class.java)?.toDomain()
            }

            expenses.forEach { expense ->
                expenseDao.insertExpense(expense.toEntity())
            }
            
            Results.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "fetchAndStoreExpenses: ${e.message}")
            Results.Error(e.getSpecificException())
        }
    }

    override suspend fun syncUnsyncedExpenses(): Results<Unit> {
        return try {
            val unsyncedExpenses = expenseDao.getAllExpense().first().filter { !it.isSynced }
            unsyncedExpenses.forEach { entity ->
                val result = insertExpenseToFirebase(entity.toDomain())
                if (result is Results.Success) {
                    expenseDao.insertExpense(entity.copy(isSynced = true))
                }
            }
            Results.Success(Unit)
        } catch (e: Exception) {
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
            "ExpenseSync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
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
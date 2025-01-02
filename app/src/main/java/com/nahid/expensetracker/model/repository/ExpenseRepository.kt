package com.nahid.expensetracker.model.repository

import android.util.Log
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.model.local.db.LocalDatabase
import kotlinx.coroutines.flow.Flow

private const val TAG = "ExpenseRepository"

class ExpenseRepository(private val localDatabase: LocalDatabase) {
    private val expanseDao = localDatabase.expanseDao()
    suspend fun insertExpanse(data: Expense) {
        Log.d(TAG, "insertExpense: $data")
        expanseDao.insertExpense(data)
    }

    fun getAllExpense(): Flow<List<Expense>>? {
        return expanseDao.getAllExpense()
    }

    suspend fun updateExpense(data: Expense) {
        expanseDao.updateExpense(data)
    }

    suspend fun deleteExpense(data: Expense) {
        expanseDao.deleteExpense(data)
    }
    fun getAllExpenseByDate(): Flow<List<Expense>>? {
        return expanseDao.getAllExpenseByDate()
    }
}
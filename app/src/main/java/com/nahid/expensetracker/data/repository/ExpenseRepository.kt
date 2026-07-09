package com.nahid.expensetracker.data.repository

import android.util.Log
import com.nahid.expensetracker.data.local.entity.Expense
import com.nahid.expensetracker.domain.model.ExpenseSummary
import com.nahid.expensetracker.data.local.AppDatabase
import kotlinx.coroutines.flow.Flow

private const val TAG = "ExpenseRepository"

class ExpenseRepository(private val appDatabase: AppDatabase) {
    private val expanseDao = appDatabase.expanseDao()
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
    fun getAllExpenseByDate(): Flow<List<ExpenseSummary>>? {
        return expanseDao.getAllExpenseByDate()
    }
    fun getTopExpense(): Flow<List<Expense>>? {
        return expanseDao.getAllTopExpense()
    }
    fun getExpenseByID(id: Int): Flow<Expense> {
        return expanseDao.getExpenseByID(id)
    }
}
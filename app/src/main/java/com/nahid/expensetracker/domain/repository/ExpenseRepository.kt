package com.nahid.expensetracker.domain.repository

import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.ExpenseCategory
import com.nahid.expensetracker.domain.model.ExpenseType
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun getExpCategory(): Results<List<ExpenseCategory>>
    suspend fun getExpenseType(): Results<List<ExpenseType>>

    suspend fun insertExpense(expense: Expense): Results<Boolean>
    suspend fun syncUnsyncedExpenses(): Results<Unit>
    fun scheduleSync()
    fun getTopExpense(): Flow<List<Expense>>
    fun getLastFiveExpense(): Flow<List<Expense>>
    fun getAllExpense(): Flow<List<Expense>>
    fun getAllFilterExpense(type: String): Flow<List<Expense>>
}
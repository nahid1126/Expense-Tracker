package com.nahid.expensetracker.model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.model.data.ExpenseSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM Expense")
    fun getAllExpense(): Flow<List<Expense>>

    @Query("SELECT * FROM Expense WHERE id = :id")
    fun getExpenseByID(id: Int): Flow<Expense>

    @Query("SELECT * FROM Expense where type ='Expense' ORDER BY amount DESC LIMIT 5 ")
    fun getAllTopExpense(): Flow<List<Expense>>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT type, date, SUM(amount) AS total_amount FROM Expense where type = :type GROUP BY type, date ORDER BY date")
    fun getAllExpenseByDate(type: String = "Expense"): Flow<List<ExpenseSummary>>
}
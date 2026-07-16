package com.nahid.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
import com.nahid.expensetracker.domain.model.ExpenseSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expenseEntity: ExpenseEntity)

    @Query("SELECT * FROM ExpenseEntity")
    fun getAllExpense(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    fun getExpenseByID(id: Int): Flow<ExpenseEntity>

    @Query("SELECT * FROM ExpenseEntity where type ='ExpenseEntity' ORDER BY amount DESC LIMIT 5 ")
    fun getAllTopExpense(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM ExpenseEntity where type =:type")
    fun getAllFilterExpense(type: String): Flow<List<ExpenseEntity>>


    @Query("SELECT * FROM ExpenseEntity ORDER BY amount DESC LIMIT 5 ")
    fun getLastFiveExpense(): Flow<List<ExpenseEntity>>

    @Update
    suspend fun updateExpense(expenseEntity: ExpenseEntity)

    @Query("DELETE FROM ExpenseEntity")
    suspend fun deleteExpense()

    @Query("SELECT type, date, SUM(amount) AS total_amount FROM ExpenseEntity where type = :type GROUP BY type, date ORDER BY date")
    fun getAllExpenseByDate(type: String = "ExpenseEntity"): Flow<List<ExpenseSummary>>

    @Query("SELECT * FROM ExpenseEntity WHERE isSynced = 0")
    suspend fun getUnsyncedExpenses(): List<ExpenseEntity>
}
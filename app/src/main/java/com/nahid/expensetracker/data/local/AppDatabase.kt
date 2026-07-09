package com.nahid.expensetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.data.local.dao.ExpenseDao
import com.nahid.expensetracker.data.local.entity.Expense

@Database(entities = [Expense::class], version = AppConstants.DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expanseDao(): ExpenseDao
}
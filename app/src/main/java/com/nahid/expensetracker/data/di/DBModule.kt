package com.nahid.expensetracker.data.di

import androidx.room.Room
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.data.local.dao.ExpenseDao
import com.nahid.expensetracker.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppConstants.DB_NAME
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    single { provideExpenseDao(get()) }

}

fun provideExpenseDao(appDatabase: AppDatabase): ExpenseDao = appDatabase.expanseDao()

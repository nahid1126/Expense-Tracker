package com.nahid.expensetracker

import android.app.Application
import com.nahid.expensetracker.data.di.appModule
import com.nahid.expensetracker.data.di.dbModule
import com.nahid.expensetracker.data.di.firebaseModule
import com.nahid.expensetracker.data.di.networkModule
import com.nahid.expensetracker.data.di.repositoryModule
import com.nahid.expensetracker.data.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ExpenseTracker : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ExpenseTracker)
            modules(appModule, networkModule, dbModule, repositoryModule, viewModelModule,
                firebaseModule
            )
        }
    }
}
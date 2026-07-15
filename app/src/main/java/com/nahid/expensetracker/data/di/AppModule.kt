package com.nahid.expensetracker.data.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.data.local.AppPreference
import com.nahid.expensetracker.data.repository.GoogleAuthManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        PreferenceDataStoreFactory.create {
            get<Context>().preferencesDataStoreFile(AppConstants.PREF_NAME)
        }
    }

    single {
        AppPreference(get())
    }

    single {
        GoogleAuthManager(get())
    }

    single { WorkManager.getInstance(androidContext()) }
}
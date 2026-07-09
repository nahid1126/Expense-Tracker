package com.nahid.expensetracker.data.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.data.local.AppPreference
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
}
package com.nahid.expensetracker.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nahid.expensetracker.domain.model.ThemeMode
import com.nahid.expensetracker.domain.model.ExpenseCategory
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreference(private val dataStore: DataStore<Preferences>) {
    companion object {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER = stringPreferencesKey("user")
        val USER_GMAIL = stringPreferencesKey("user_gmail")
        val USER_NAME = stringPreferencesKey("user_name")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val LEGACY_DARK_MODE = booleanPreferencesKey("mode")
        private val EXPENSE_CATEGORIES = stringPreferencesKey("expense_categories")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAS_LOCATION = booleanPreferencesKey("has_location")
    }


    suspend fun saveUserName(name: String): Boolean {
        return try {
            dataStore.edit {
                it[USER_NAME] = name
            }
            true
        } catch (e: Exception) {
            return false
        }
    }

    fun readUserName(): Flow<String> {
        return dataStore.data.map {
            it[USER_NAME] ?: ""
        }
    }


    suspend fun saveUserId(userId: String): Boolean {
        return try {
            dataStore.edit {
                it[USER_ID] = userId
            }
            true
        } catch (e: Exception) {
            return false
        }
    }

    fun readUserId(): Flow<String> {
        return dataStore.data.map {
            it[USER_ID] ?: ""
        }
    }

    suspend fun saveUserGmail(gmail: String?): Boolean {
        return try {
            dataStore.edit {
                it[USER_GMAIL] = gmail ?: "test@gmail.com"
            }
            true
        } catch (e: Exception) {
            return false
        }
    }

    fun readUserGmail(): Flow<String> {
        return dataStore.data.map {
            it[USER_GMAIL] ?: ""
        }
    }

    suspend fun saveExpenseCategories(categories: List<ExpenseCategory>) {
        dataStore.edit { it[EXPENSE_CATEGORIES] = Json.encodeToString(categories) }
    }

    fun readExpenseCategories(): Flow<List<ExpenseCategory>> = dataStore.data.map { preferences ->
        val encoded = preferences[EXPENSE_CATEGORIES] ?: return@map emptyList()
        try {
            Json.decodeFromString(encoded)
        } catch (_: SerializationException) {
            emptyList()
        }
    }

    suspend fun saveThemeMode(mode: ThemeMode) {
        dataStore.edit {
            it[THEME_MODE] = mode.name
            it.remove(LEGACY_DARK_MODE)
        }
    }

    fun readThemeMode(): Flow<ThemeMode> = dataStore.data.map { preferences ->
        ThemeMode.entries.firstOrNull { it.name == preferences[THEME_MODE] }
            ?: when (preferences[LEGACY_DARK_MODE]) {
                true -> ThemeMode.DARK
                false -> ThemeMode.LIGHT
                null -> ThemeMode.SYSTEM
            }
    }

    suspend fun putHasLocation(hasLocation: Boolean): Boolean {
        return try {
            dataStore.edit {
                it[HAS_LOCATION] = hasLocation
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getHasLocation(): Flow<Boolean> {
        return dataStore.data.map {
            it[HAS_LOCATION] ?: false
        }
    }

    suspend fun clearAll() {
        dataStore.edit {
            it.remove(USER_ID)
            it.remove(USER_GMAIL)
            it.remove(USER_NAME)
            it.remove(TOKEN)
            it.remove(REFRESH_TOKEN)
        }
    }
}
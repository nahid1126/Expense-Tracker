package com.nahid.expensetracker.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseUser
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
        val MODE = booleanPreferencesKey("mode")
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

    suspend fun isDarkMode(isDark: Boolean): Boolean {
        return try {
            dataStore.edit {
                it[MODE] = isDark
            }
            true
        } catch (e: Exception) {
            return false
        }
    }

    fun readIsDarkMode(): Flow<Boolean> {
        return dataStore.data.map {
            it[MODE] ?: false
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
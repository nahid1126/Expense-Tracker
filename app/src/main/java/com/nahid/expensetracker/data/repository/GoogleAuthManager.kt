package com.nahid.expensetracker.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.core.Logger

class GoogleAuthManager(
    private val context: Context,
) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): String? {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(AppConstants.WEB_CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context,
                request
            )

            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {

                GoogleIdTokenCredential
                    .createFrom(credential.data)
                    .idToken
            } else {
                null
            }
        } catch (e: GetCredentialException) {
            Logger.log("GoogleAuthManager", "Error getting credential: ${e.message}")
            null
        } catch (e: Exception) {
            Logger.log("GoogleAuthManager", "Unexpected error: ${e.message}")
            null
        }
    }
}
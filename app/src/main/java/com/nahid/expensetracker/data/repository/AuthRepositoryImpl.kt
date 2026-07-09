package com.nahid.expensetracker.data.repository

import android.util.Log
import androidx.credentials.exceptions.GetCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.nahid.expensetracker.core.Logger
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.core.utils.exception.AppException
import com.nahid.expensetracker.core.utils.extension.getSpecificException
import com.nahid.expensetracker.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthRepositoryImpl"

class AuthRepositoryImpl(private val auth: FirebaseAuth) : AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): Results<FirebaseUser?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Log.d(TAG, "signInWithGoogle: $credential")
            val result = auth.signInWithCredential(credential).await()
            if (result.user != null) {
                Log.d(TAG, "signInWithGoogle: ${result.user!!.uid}")
                Results.Success(result.user)
            } else {
                Results.Error(AppException.AuthException())
            }
        } catch (e: GetCredentialException) {
            Results.Error(
                AppException.OthersException(
                    e.message.toString() ?: "Google sign in failed"
                )
            )
        } catch (e: Exception) {
            Log.d(TAG, "signInWithGoogle: ${e.getSpecificException()}")
            Results.Error(e.getSpecificException())
        }
    }
}
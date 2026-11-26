package com.nahid.expensetracker.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.nahid.expensetracker.core.Results

interface AuthRepository {
    fun hasActiveSession(): Boolean
    suspend fun signInWithGoogle(idToken: String): Results<FirebaseUser?>
    suspend fun logout(): Results<Boolean>
}
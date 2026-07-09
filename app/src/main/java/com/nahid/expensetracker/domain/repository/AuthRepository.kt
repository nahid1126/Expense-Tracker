package com.nahid.expensetracker.domain.repository

import com.google.firebase.auth.FirebaseUser
import com.nahid.expensetracker.core.Results

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Results<FirebaseUser?>
    suspend fun logout(): Results<Boolean>
}
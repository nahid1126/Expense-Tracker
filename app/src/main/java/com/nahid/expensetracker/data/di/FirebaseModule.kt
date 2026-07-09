package com.nahid.expensetracker.data.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module


val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    /*single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }*/
}
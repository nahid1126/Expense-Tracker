package com.nahid.expensetracker.data.di

import com.nahid.expensetracker.data.repository.AuthRepositoryImpl
import com.nahid.expensetracker.data.repository.ExpenseRepositoryImpl
import com.nahid.expensetracker.domain.repository.AuthRepository
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ExpenseRepository> { ExpenseRepositoryImpl(get(), get(), get(), get(), get()) }
    /*single <LocationRepository>{ it
        LocationRepositoryImpl(get())
    }
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get()) }
    */
}

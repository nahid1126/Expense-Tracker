package com.nahid.expensetracker.data.di

import com.nahid.expensetracker.data.repository.AuthRepositoryImpl
import com.nahid.expensetracker.domain.repository.AuthRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    /*single <LocationRepository>{ it
        LocationRepositoryImpl(get())
    }
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get()) }
    */
}

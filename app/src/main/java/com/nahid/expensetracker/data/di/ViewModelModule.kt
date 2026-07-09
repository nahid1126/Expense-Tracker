package com.nahid.expensetracker.data.di

import com.nahid.expensetracker.ui.presentation.auth.login.LoginViewModel
import com.nahid.expensetracker.ui.presentation.home.HomeViewModel
import com.nahid.expensetracker.ui.presentation.main.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
}

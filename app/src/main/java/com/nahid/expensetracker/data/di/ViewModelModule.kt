package com.nahid.expensetracker.data.di

import com.nahid.expensetracker.ui.presentation.addexpense.AddExpenseViewModel
import com.nahid.expensetracker.ui.presentation.auth.login.LoginViewModel
import com.nahid.expensetracker.ui.presentation.home.HomeViewModel
import com.nahid.expensetracker.ui.presentation.main.MainViewModel
import com.nahid.expensetracker.ui.presentation.transections.TransectionsViewModel
import com.nahid.expensetracker.ui.presentation.updateexpense.UpdateExpenseViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::TransectionsViewModel)
    viewModelOf(::AddExpenseViewModel)
    viewModelOf(::UpdateExpenseViewModel)
}

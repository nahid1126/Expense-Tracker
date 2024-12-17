package com.nahid.expensetracker.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.model.local.db.LocalDatabase
import com.nahid.expensetracker.model.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    val message = MutableSharedFlow<String>()
    fun addExpanse(expense: Expense) {
        viewModelScope.launch {
            if (expense.title.isEmpty()) {
                message.emit("Please enter title")
            } else if (expense.type.isEmpty()) {
                message.emit("Please select type")
            } else if (expense.amount.toString().trim().isEmpty()) {
                message.emit("Please enter amount")
            } else if (expense.category.isEmpty()) {
                message.emit("Please select category")
            } else if (expense.date.isEmpty()) {
                message.emit("Please select date")
            } else {
                message.emit("Expense added successfully")
                repository.insertExpanse(expense)
            }
        }
    }
}

class AddExpenseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddExpenseViewModel(ExpenseRepository(LocalDatabase.getDataBase(context))) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
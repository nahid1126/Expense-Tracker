package com.nahid.expensetracker.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.model.local.db.LocalDatabase
import com.nahid.expensetracker.model.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "AddExpenseViewModel"
class AddExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    val message = MutableSharedFlow<String>()
    var finalExpense = MutableStateFlow<Expense?>(null)
    fun addExpanse(expense: Expense, isForUpdate: Boolean) {
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
                message.emit("Expense ${if (isForUpdate) "updated" else "added"} successfully")
                if (isForUpdate) {
                    repository.updateExpense(expense)
                } else {
                    repository.insertExpanse(expense)
                }
            }
        }
    }

    fun getExpenseByID(id: Int) {
        val expense = repository.getExpenseByID(id).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(), null
        )
        viewModelScope.launch {
            expense.collect {
                it?.let {
                    finalExpense.value = it
                    Log.d(TAG, "getExpenseByID: ${finalExpense.value}")
                }
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
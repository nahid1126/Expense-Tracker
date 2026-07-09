package com.nahid.expensetracker.ui.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.data.local.entity.Expense
import com.nahid.expensetracker.data.local.AppDatabase
import com.nahid.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: ExpenseRepository) : ViewModel() {
    var expenseList = MutableStateFlow<List<Expense>>(emptyList())
        private set


    init {
        viewModelScope.launch {
            getAllExpense().collectLatest {
                if (it.isNotEmpty()) {
                    Log.d(TAG, "GetExpense: $it")
                    expenseList.value = it
                }
            }
        }
    }


    private fun getAllExpense() = repository.getAllExpense()!!.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun getBalance(): String {
        val balance =
            expenseList.value.sumOf { if (it.type == "Income") it.amount else -it.amount }
        Log.d(TAG, "getBalance: $balance")
        return "৳ $balance"
    }

    fun getIncome(): String {
        val income = expenseList.value.filter { it.type == "Income" }.sumOf { it.amount }
        Log.d(TAG, "getIncome: $income")
        return "৳ $income"
    }

    fun getExpense(): String {
        val expense = expenseList.value.filter { it.type == "Expense" }.sumOf { it.amount }
        Log.d(TAG, "getExpense: $expense")
        return "৳ $expense"
    }

}
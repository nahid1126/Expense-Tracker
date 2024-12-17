package com.nahid.expensetracker.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.R
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.model.local.db.LocalDatabase
import com.nahid.expensetracker.model.repository.ExpenseRepository
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

    fun getIcon(expense: Expense): Int {
        return when (expense.category) {
            "Salary" -> {
                R.drawable.ic_up
            }
            "Basa", "Kalamoni" -> {
                R.drawable.fixed
            }
            "Transport" -> {
                R.drawable.transport
            }
            else -> {
                R.drawable.others
            }
        }
    }

}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(ExpenseRepository(LocalDatabase.getDataBase(context))) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
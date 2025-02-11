package com.nahid.expensetracker.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.nahid.expensetracker.Utils
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.model.data.ExpenseSummary
import com.nahid.expensetracker.model.local.db.LocalDatabase
import com.nahid.expensetracker.model.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "StatsViewModel"
class StatsViewModel(private val repository: ExpenseRepository) : ViewModel() {
    var expenseList = MutableStateFlow<List<ExpenseSummary>>(emptyList())
        private set


    init {
        viewModelScope.launch {
            getAllExpenseByDate().collectLatest {
                if (it.isNotEmpty()) {
                    Log.d(TAG, "GetExpense: $it")
                    expenseList.value = it
                }
            }
        }
    }

    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry> {
        val list = mutableListOf<Entry>()
        entries.forEach {
            list.add(Entry(Utils.formatMilliFromDate(it.date).toFloat(), it.total_amount.toFloat()))
        }
        return list
    }

    private fun getAllExpenseByDate() = repository.getAllExpenseByDate()!!.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(),
        emptyList()
    )
}

class StatsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(ExpenseRepository(LocalDatabase.getDataBase(context))) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
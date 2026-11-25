package com.nahid.expensetracker.ui.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.recentTransactionOrder
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {
    private val exitDialogVisible = MutableStateFlow(false)

    val uiState = combine(exitDialogVisible, expenseRepository.getAllExpense()) { showExitDialog, expenses ->
        val income = expenses.filter { it.type == "Income" }.sumOf { it.amount.toLong() }
        val outgoing = expenses.filter { it.type == "Expense" }.sumOf { it.amount.toLong() }
        HomeUiState(
            showExitDialog = showExitDialog,
            totalIncome = income,
            totalExpense = outgoing,
            totalBalance = income - outgoing,
            recentTransactions = expenses.sortedWith(recentTransactionOrder).take(5),
            spending = spendingBreakdown(expenses),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500, replayExpirationMillis = 0), HomeUiState())

    fun getInitialData() {
        viewModelScope.launch { expenseRepository.fetchAndStoreExpenses() }
    }

    fun showExitDialog(visible: Boolean) {
        exitDialogVisible.value = visible
    }
}

data class HomeUiState(
    val showExitDialog: Boolean = false,
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val totalBalance: Long = 0,
    val recentTransactions: List<Expense> = emptyList(),
    val spending: List<CategorySpending> = emptyList(),
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Home",
        dateTitle = "",
        showTopBar = true,
        showNavigation = false,
        showOptionMenu = true,
    ),
)

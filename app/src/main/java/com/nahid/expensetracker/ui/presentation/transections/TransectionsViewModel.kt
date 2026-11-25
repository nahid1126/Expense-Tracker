package com.nahid.expensetracker.ui.presentation.transections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.asTransactionDate
import com.nahid.expensetracker.domain.model.isInMonth
import com.nahid.expensetracker.domain.model.recentTransactionOrder
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransectionsViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {
    private val filters = MutableStateFlow(TransectionsUiState())
    private val messages = MutableSharedFlow<String>()

    val uiState = combine(filters, expenseRepository.getAllExpense()) { state, expenses ->
        val availableMonths = (expenses.mapNotNull {
            it.date.asTransactionDate()?.let(YearMonth::from)
        } + YearMonth.now()).distinct().sortedDescending()
        state.copy(
            months = availableMonths,
            transactions = expenses.filter { expense ->
                (state.type == null || expense.type == state.type) &&
                    (state.month == null || expense.isInMonth(state.month))
            }.sortedWith(recentTransactionOrder),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), filters.value)

    val uiMessage = messages.asSharedFlow()

    fun selectType(type: String?) = filters.update { it.copy(type = type) }
    fun selectMonth(month: YearMonth?) = filters.update { it.copy(month = month) }
    fun requestDelete(expense: Expense?) = filters.update { it.copy(pendingDelete = expense) }

    fun deleteExpense() {
        val expense = filters.value.pendingDelete ?: return
        requestDelete(null)
        viewModelScope.launch {
            val message = when (val result = expenseRepository.deleteExpense(expense)) {
                is Results.Success -> "Transaction deleted"
                is Results.Error -> result.exception.message
            }
            messages.emit(message)
        }
    }
}

data class TransectionsUiState(
    val type: String? = null,
    val month: YearMonth? = YearMonth.now(),
    val months: List<YearMonth> = listOf(YearMonth.now()),
    val transactions: List<Expense> = emptyList(),
    val pendingDelete: Expense? = null,
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Transactions",
        showTopBar = true,
        showNavigation = true,
        showSubTitle = false,
    ),
)

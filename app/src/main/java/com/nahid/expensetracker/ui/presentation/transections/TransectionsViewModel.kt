package com.nahid.expensetracker.ui.presentation.transections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.home.HomeUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransectionsViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {
    private val mutableUiState = MutableStateFlow(TransectionsUiState())
    private val mutableUiEvent = MutableSharedFlow<TransectionsUiEvent>()

    val uiState = combine(
        mutableUiState, expenseRepository.getAllExpense()
    ) { state, expense ->
        val filterExpend = expense.filter { it.type == state.selectedTab }
        state.copy(
            incomeExpenseList = filterExpend
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), TransectionsUiState())

    val uiEvent: SharedFlow<TransectionsUiEvent> = mutableUiEvent.asSharedFlow()

    fun updateUiState(uiState: TransectionsUiState) {
        mutableUiState.value = uiState
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(TransectionsUiEvent.ShowMessage(Pair(isSuccess, message)))
        }
    }
}

sealed interface TransectionsUiEvent {
    data class ShowMessage(val message: Pair<Boolean, String>) : TransectionsUiEvent
}

data class TransectionsUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val selectedTab: String? = null,
    val incomeExpenseList: List<Expense> = arrayListOf(),
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Transections",
        showTopBar = true,
        showNavigation = true,
        showSubTitle = false
    ),
)
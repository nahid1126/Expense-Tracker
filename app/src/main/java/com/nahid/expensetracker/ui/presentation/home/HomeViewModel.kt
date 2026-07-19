package com.nahid.expensetracker.ui.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.User
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val mutableUiState = MutableStateFlow(HomeUiState())
    private val mutableUiEvent = MutableSharedFlow<HomeUiEvent>()

    val uiState: StateFlow<HomeUiState> = combine(
        mutableUiState,
        expenseRepository.getLastFiveExpense(),
        expenseRepository.getAllExpense()
    ) { state, lastFive, allExpenses ->
        val totalIncome = allExpenses.filter { it.type == "Income" }.sumOf { it.amount }
        val totalExpenseEntity = allExpenses.filter { it.type == "Expense" }.sumOf { it.amount }
        val totalBalance = totalIncome - totalExpenseEntity
        
        state.copy(
            topExpenseEntityList = lastFive,
            totalIncome = totalIncome.toDouble(),
            totalExpense = totalExpenseEntity.toDouble(),
            totalBalance = totalBalance.toDouble()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), HomeUiState())
    val uiEvent: SharedFlow<HomeUiEvent> = mutableUiEvent.asSharedFlow()

    fun getInitialData() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true) }
            val fetchExpense = async { expenseRepository.fetchAndStoreExpenses() }
            val expenseResponse = fetchExpense.await()

            when {
                expenseResponse is Results.Success -> {
                    mutableUiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }

                expenseResponse is Results.Error -> {
                    mutableUiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    showMessage(false, expenseResponse.exception.message)
                }
            }
        }
    }

    fun updateUiState(uiState: HomeUiState) {
        mutableUiState.value = uiState
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(HomeUiEvent.ShowMessage(Pair(isSuccess, message)))
        }
    }

}
sealed interface HomeUiEvent {
    data class ShowMessage(val message: Pair<Boolean, String>) : HomeUiEvent
}

data class HomeUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val user: User? = null,
    val message: String? = null,
    val userName: String? = null,
    val showLogoutDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val title: String = "Home",
    val loggedOut: Boolean = false,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalBalance: Double = 0.0,
    val topExpenseEntityList: List<Expense> = arrayListOf(),
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Home",
        dateTitle = "",
        showTopBar = true,
        showNavigation = false,
        showOptionMenu = true
    ),
)
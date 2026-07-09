package com.nahid.expensetracker.ui.presentation.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nahid.expensetracker.data.local.entity.Expense
import com.nahid.expensetracker.data.local.AppDatabase
import com.nahid.expensetracker.data.repository.ExpenseRepository
import com.nahid.expensetracker.domain.model.User
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.auth.login.LoginUiState
import com.nahid.expensetracker.ui.presentation.auth.login.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"

class HomeViewModel() : ViewModel() {
    /*var expenseList = MutableStateFlow<List<Expense>>(emptyList())
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
    }*/

    private val mutableUiState = MutableStateFlow(HomeUiState())
    private val mutableUiEvent = MutableSharedFlow<HomeUiEvent>()

    val uiState: StateFlow<HomeUiState> = mutableUiState.asStateFlow()
    val uiEvent: SharedFlow<HomeUiEvent> = mutableUiEvent.asSharedFlow()

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
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Home",
        dateTitle = "",
        showTopBar = true,
        showNavigation = false,
        showOptionMenu = true
    ),
)
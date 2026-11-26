package com.nahid.expensetracker.ui.presentation.updateexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.validTransactionAmount
import com.nahid.expensetracker.domain.model.transactionDateFromPicker
import com.nahid.expensetracker.domain.model.asTransactionDate
import com.nahid.expensetracker.domain.model.ExpenseCategory
import com.nahid.expensetracker.domain.model.ExpenseType
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.domain.model.toPickerMillis
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "AddExpenseViewModel"

class UpdateExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(UpdateExpenseUiState())
    private val mutableUiEvent = MutableSharedFlow<UpdateExpenseUiEvent>()

    val uiState: StateFlow<UpdateExpenseUiState> = mutableUiState.asStateFlow()
    val uiEvent: SharedFlow<UpdateExpenseUiEvent> = mutableUiEvent.asSharedFlow()



    fun updateUiState(transform: (UpdateExpenseUiState) -> UpdateExpenseUiState) {
        mutableUiState.update(transform)
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(UpdateExpenseUiEvent.ShowMessage(Pair(isSuccess, message)))
        }
    }


    fun getInitialData() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true) }
            val expCategory =
                async {
                    expenseRepository.getExpCategory()
                }
            val expType =
                async {
                    expenseRepository.getExpenseType()
                }

            val expCatResponse = expCategory.await()
            val expTypeResponse = expType.await()

            when {
                expCatResponse is Results.Success && expTypeResponse is Results.Success -> {
                    mutableUiState.update {
                        it.copy(
                            isLoading = false,
                            expCategories = expCatResponse.data,
                            expTypes = expTypeResponse.data

                        )
                    }
                }

                expCatResponse is Results.Error -> {
                    mutableUiState.update {
                        it.copy(isLoading = false)
                    }
                    showMessage(false, expCatResponse.exception.message)
                }

                expTypeResponse is Results.Error -> {
                    mutableUiState.update {
                        it.copy(isLoading = false)
                    }
                    showMessage(false, expTypeResponse.exception.message)
                }
            }

        }
    }

    fun addExpense() {
        val mainState = uiState.value
        if (mainState.isLoading) return
        mutableUiState.update { it.copy(isLoading = true) }
        val amount = mainState.amount.validTransactionAmount()
        viewModelScope.launch {
            if (mainState.expTitle.isNullOrBlank()) {
                showMessage(false, "Expense Title Required")
                mutableUiState.update { it.copy(isLoading = false) }
            } else if (mainState.selectedExpType.isNullOrEmpty()) {
                showMessage(false, "Expense Type Required")
                mutableUiState.update { it.copy(isLoading = false) }
            } else if (mainState.selectedExpCategory.isNullOrEmpty()) {
                showMessage(false, "Expense Category Required")
                mutableUiState.update { it.copy(isLoading = false) }
            } else if (amount == null) {
                showMessage(false, "Expense Amount Required")
                mutableUiState.update { it.copy(isLoading = false) }
            } else if (mainState.selectedExpDate == 0L) {
                showMessage(false, "Expense Date Required")
                mutableUiState.update { it.copy(isLoading = false) }
            } else {
                val expense = Expense(
                    id = mainState.expenseId,
                    title = mainState.expTitle.trim(),
                    amount = amount,
                    date = transactionDateFromPicker(mainState.selectedExpDate).toString(),
                    type = mainState.selectedExpType,
                    category = mainState.selectedExpCategory
                )

                val result = expenseRepository.updateExpense(expense)
                when (result) {
                    is Results.Success -> {
                        mutableUiEvent.emit(UpdateExpenseUiEvent.ShowMessage(true to "Saved on this device"))
                        mutableUiState.update { it.copy(isLoading = false) }
                        mutableUiEvent.emit(UpdateExpenseUiEvent.NavigateBack)
                    }

                    is Results.Error -> {
                        showMessage(false, "Error: ${result.exception.message}")
                        mutableUiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun setInitialData(expense: Expense) {
        mutableUiState.update { state ->
            state.copy(
                expenseId = expense.id?:0,
                expTitle = expense.title,
                amount = expense.amount.toString(),
                selectedExpDate = expense.date.asTransactionDate()?.toPickerMillis() ?: 0L,
                selectedExpType = expense.type,
                selectedExpCategory = expense.category,
                uiConfig = state.uiConfig.copy(title = "Edit transaction")
            )
        }
    }
}

sealed interface UpdateExpenseUiEvent {
    data class ShowMessage(val message: Pair<Boolean, String>) : UpdateExpenseUiEvent
    data object NavigateBack : UpdateExpenseUiEvent
}

data class UpdateExpenseUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val expenseId: Int = 0,
    val expTitle: String? = null,
    val showExpDatePicker: Boolean = false,
    val selectedExpDate: Long = 0L,
    val selectedExpType: String? = null,
    val selectedExpTypeId: Long = 0,
    val amount: String = "",
    val selectedExpCategory: String? = null,
    val selectedExpCategoryId: Long = 0,
    val expCategories: List<ExpenseCategory> = arrayListOf(),
    val expTypes: List<ExpenseType> = arrayListOf(),
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Edit transaction",
        showTopBar = true,
        showNavigation = true,
        showSubTitle = false
    ),
)
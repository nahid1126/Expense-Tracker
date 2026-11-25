package com.nahid.expensetracker.ui.presentation.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.validTransactionAmount
import com.nahid.expensetracker.domain.model.transactionDateFromPicker
import com.nahid.expensetracker.domain.model.ExpenseCategory
import com.nahid.expensetracker.domain.model.ExpenseType
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import java.time.LocalDate
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

class AddExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(AddExpenseUiState())
    private val mutableUiEvent = MutableSharedFlow<AddExpenseUiEvent>()

    val uiState: StateFlow<AddExpenseUiState> = mutableUiState.asStateFlow()
    val uiEvent: SharedFlow<AddExpenseUiEvent> = mutableUiEvent.asSharedFlow()


    fun updateUiState(transform: (AddExpenseUiState) -> AddExpenseUiState) {
        mutableUiState.update(transform)
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(AddExpenseUiEvent.ShowMessage(Pair(isSuccess, message)))
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
                    id = null,
                    title = mainState.expTitle.trim(),
                    amount = amount,
                    date = transactionDateFromPicker(mainState.selectedExpDate).toString(),
                    type = mainState.selectedExpType,
                    category = mainState.selectedExpCategory
                )

                when (val result = expenseRepository.insertExpense(expense)) {
                    is Results.Success -> {
                        showMessage(true, "Expense added successfully")
                        mutableUiState.update { it.copy(isLoading = false) }
                        mutableUiEvent.emit(AddExpenseUiEvent.NavigateBack)
                    }

                    is Results.Error -> {
                        showMessage(false, "Error: ${result.exception.message}")
                        mutableUiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }
}

sealed interface AddExpenseUiEvent {
    data class ShowMessage(val message: Pair<Boolean, String>) : AddExpenseUiEvent
    data object NavigateBack : AddExpenseUiEvent
}

data class AddExpenseUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val expenseId: Int? = null,
    val expTitle: String? = null,
    val showExpDatePicker: Boolean = false,
    val selectedExpDate: Long = LocalDate.now().toPickerMillis(),
    val selectedExpType: String? = null,
    val selectedExpTypeId: Long = 0,
    val amount: String = "",
    val selectedExpCategory: String? = null,
    val selectedExpCategoryId: Long = 0,
    val expCategories: List<ExpenseCategory> = arrayListOf(),
    val expTypes: List<ExpenseType> = arrayListOf(),
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Add transaction",
        showTopBar = true,
        showNavigation = true,
        showSubTitle = false
    ),
)
package com.nahid.expensetracker.ui.presentation.updateexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.core.utils.extension.longToSimpleDateFormatString
import com.nahid.expensetracker.core.utils.extension.toDateMillis
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.ExpenseCategory
import com.nahid.expensetracker.domain.model.ExpenseType
import com.nahid.expensetracker.domain.repository.ExpenseRepository
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
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



    fun updateUiState(uiState: UpdateExpenseUiState) {
        mutableUiState.value = uiState
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
        updateUiState(uiState.value.copy(isLoading = true))
        val mainState = uiState.value
        viewModelScope.launch {
            if (mainState.expTitle.isNullOrEmpty()) {
                showMessage(false, "Expense Title Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.selectedExpType.isNullOrEmpty()) {
                showMessage(false, "Expense Type Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.selectedExpCategory.isNullOrEmpty()) {
                showMessage(false, "Expense Category Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.amount.isEmpty() || mainState.amount.toIntOrNull() == 0) {
                showMessage(false, "Expense Amount Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.selectedExpDate == 0L) {
                showMessage(false, "Expense Date Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else {
                val expense = Expense(
                    id = mainState.expenseId,
                    title = mainState.expTitle,
                    amount = mainState.amount.toIntOrNull() ?: 0,
                    date = mainState.selectedExpDate.longToSimpleDateFormatString(),
                    type = mainState.selectedExpType,
                    category = mainState.selectedExpCategory
                )

                val result = expenseRepository.updateExpense(expense)
                when (result) {
                    is Results.Success -> {
                        showMessage(true, "Expense updated successfully")
                        updateUiState(uiState.value.copy(isLoading = false))
                        mutableUiEvent.emit(UpdateExpenseUiEvent.NavigateBack)
                    }

                    is Results.Error -> {
                        showMessage(false, "Error: ${result.exception.message}")
                        updateUiState(uiState.value.copy(isLoading = false))
                    }
                }
            }
        }
    }

    fun setInitialData(expense: Expense) {
        updateUiState(
            uiState.value.copy(
                expenseId = expense.id?:0,
                expTitle = expense.title,
                amount = expense.amount.toString(),
                selectedExpDate = expense.date.toDateMillis(),
                selectedExpType = expense.type,
                selectedExpCategory = expense.category,
                uiConfig = uiState.value.uiConfig.copy(title = "Update Expense")
            )
        )
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
        title = "Update Expense",
        showTopBar = true,
        showNavigation = true,
        showSubTitle = false
    ),
)
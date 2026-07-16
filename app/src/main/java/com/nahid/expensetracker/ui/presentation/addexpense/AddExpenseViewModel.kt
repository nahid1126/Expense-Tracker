package com.nahid.expensetracker.ui.presentation.addexpense

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nahid.expensetracker.core.Results
import com.nahid.expensetracker.core.utils.extension.longToSimpleDateFormatString
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
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

class AddExpenseViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {
    private val mutableUiState = MutableStateFlow(AddExpenseUiState())
    private val mutableUiEvent = MutableSharedFlow<AddExpenseUiEvent>()

    val uiState: StateFlow<AddExpenseUiState> = mutableUiState.asStateFlow()
    val uiEvent: SharedFlow<AddExpenseUiEvent> = mutableUiEvent.asSharedFlow()

    fun updateUiState(uiState: AddExpenseUiState) {
        mutableUiState.value = uiState
    }

    fun showMessage(isSuccess: Boolean, message: String) {
        viewModelScope.launch {
            mutableUiEvent.emit(AddExpenseUiEvent.ShowMessage(Pair(isSuccess, message)))
        }
    }

    init {
        viewModelScope.launch {
            val cat = expenseRepository.getExpCategory()
            Log.d(TAG, "expCat: $cat")
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
                showMessage(false, "ExpenseEntity Title Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.selectedExpType.isNullOrEmpty()) {
                showMessage(false, "ExpenseEntity Type Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.selectedExpCategory.isNullOrEmpty()) {
                showMessage(false, "ExpenseEntity Category Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.amount == 0) {
                showMessage(false, "ExpenseEntity Amount Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else if (mainState.selectedExpDate == 0L) {
                showMessage(false, "ExpenseEntity Date Required")
                updateUiState(uiState.value.copy(isLoading = false))
            } else {
                val expense = Expense(
                    id = null,
                    title = mainState.expTitle,
                    amount = mainState.amount,
                    date = mainState.selectedExpDate.longToSimpleDateFormatString(),
                    type = mainState.selectedExpType,
                    category = mainState.selectedExpCategory
                )

                when (val result = expenseRepository.insertExpense(expense)) {
                    is Results.Success -> {
                        showMessage(true, "ExpenseEntity added successfully")
                        //expenseRepository.scheduleSync()
                        updateUiState(uiState.value.copy(isLoading = false))
                        mutableUiEvent.emit(AddExpenseUiEvent.NavigateBack)
                    }

                    is Results.Error -> {
                        showMessage(false, "Error: ${result.exception.message}")
                        updateUiState(uiState.value.copy(isLoading = false))
                    }
                }
            }
        }
    }


    /*  var finalExpense = MutableStateFlow<ExpenseEntity?>(null)
      fun addExpanse(expense: ExpenseEntity, isForUpdate: Boolean) {
          viewModelScope.launch {
              if (expense.title.isEmpty()) {
                  message.emit("Please enter title")
              } else if (expense.type.isEmpty()) {
                  message.emit("Please select type")
              } else if (expense.amount.toString().trim().isEmpty()) {
                  message.emit("Please enter amount")
              } else if (expense.category.isEmpty()) {
                  message.emit("Please select category")
              } else if (expense.date.isEmpty()) {
                  message.emit("Please select date")
              } else {
                  message.emit("ExpenseEntity ${if (isForUpdate) "updated" else "added"} successfully")
                  if (isForUpdate) {
                      repository.updateExpense(expense)
                  } else {
                      repository.insertExpanse(expense)
                  }
              }
          }
      }*/

    /*fun getExpenseByID(id: Int) {
        val expense = repository.getExpenseByID(id).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(), null
        )
        viewModelScope.launch {
            expense.collect {
                it?.let {
                    finalExpense.value = it
                    Log.d(TAG, "getExpenseByID: ${finalExpense.value}")
                }
            }
        }
    }*/
}

sealed interface AddExpenseUiEvent {
    data class ShowMessage(val message: Pair<Boolean, String>) : AddExpenseUiEvent
    data object NavigateBack : AddExpenseUiEvent
}

data class AddExpenseUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val expTitle: String? = null,
    val showExpDatePicker: Boolean = false,
    val selectedExpDate: Long = 0L,
    val selectedExpType: String? = null,
    val selectedExpTypeId: Long = 0,
    var amount: Int = 0,
    val selectedExpCategory: String? = null,
    val selectedExpCategoryId: Long = 0,
    val expCategories: List<ExpenseCategory> = arrayListOf(),
    val expTypes: List<ExpenseType> = arrayListOf(),
    val uiConfig: MainUIConfig = MainUIConfig(
        title = "Add ExpenseEntity",
        showTopBar = true,
        showNavigation = true,
        showSubTitle = false
    ),
)
package com.nahid.expensetracker.ui.presentation.addexpense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.TransactionForm
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel = koinViewModel(),
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        onChangeConfiguration(state.uiConfig)
        viewModel.getInitialData()
    }
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect {
            when (it) {
                is AddExpenseUiEvent.ShowMessage -> onShowMessage(it.message.second)
                AddExpenseUiEvent.NavigateBack -> onBack()
            }
        }
    }

    TransactionForm(
        title = state.expTitle.orEmpty(),
        amount = state.amount,
        type = state.selectedExpType,
        category = state.selectedExpCategory,
        categories = state.expCategories.map { it.name },
        date = state.selectedExpDate,
        isBusy = state.isLoading,
        saveLabel = "Save transaction",
        onTitleChange = { viewModel.updateUiState { state -> state.copy(expTitle = it) } },
        onAmountChange = { viewModel.updateUiState { state -> state.copy(amount = it) } },
        onTypeChange = { viewModel.updateUiState { state -> state.copy(selectedExpType = it) } },
        onCategoryChange = { viewModel.updateUiState { state -> state.copy(selectedExpCategory = it) } },
        onDateChange = { viewModel.updateUiState { state -> state.copy(selectedExpDate = it) } },
        onSave = viewModel::addExpense,
    )
}

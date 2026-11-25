package com.nahid.expensetracker.ui.presentation.updateexpense

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.TransactionForm
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UpdateExpenseScreen(
    viewModel: UpdateExpenseViewModel = koinViewModel(),
    expense: Expense,
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(expense.id) {
        onChangeConfiguration(state.uiConfig)
        if (viewModel.uiState.value.expenseId != expense.id) viewModel.setInitialData(expense)
        viewModel.getInitialData()
    }
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect {
            when (it) {
                is UpdateExpenseUiEvent.ShowMessage -> onShowMessage(it.message.second)
                UpdateExpenseUiEvent.NavigateBack -> onBack()
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
        saveLabel = "Save changes",
        onTitleChange = { viewModel.updateUiState { state -> state.copy(expTitle = it) } },
        onAmountChange = { viewModel.updateUiState { state -> state.copy(amount = it) } },
        onTypeChange = { viewModel.updateUiState { state -> state.copy(selectedExpType = it) } },
        onCategoryChange = { viewModel.updateUiState { state -> state.copy(selectedExpCategory = it) } },
        onDateChange = { viewModel.updateUiState { state -> state.copy(selectedExpDate = it) } },
        onSave = viewModel::addExpense,
    )
}

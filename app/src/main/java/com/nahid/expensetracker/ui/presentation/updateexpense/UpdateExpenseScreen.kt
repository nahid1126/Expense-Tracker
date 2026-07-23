@file:OptIn(ExperimentalMaterial3Api::class)

package com.nahid.expensetracker.ui.presentation.updateexpense

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyLira
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.R
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.core.utils.extension.longToSimpleDateFormatString
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.presentation.component.CustomDatePickerDialog
import com.nahid.expensetracker.ui.presentation.component.QuestionSelection
import com.nahid.expensetracker.ui.presentation.component.input_field.StandardInputField
import com.nahid.expensetracker.ui.theme.PurpleGrey80
import org.koin.compose.viewmodel.koinViewModel

private const val TAG = "AddExpanseScreen"

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun UpdateExpenseScreen(
    viewModel: UpdateExpenseViewModel = koinViewModel(),
    expense: Expense,
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        onChangeConfiguration(
            state.uiConfig
        )
        viewModel.getInitialData()
        // delay(200)
        //startAnimation = true
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is UpdateExpenseUiEvent.ShowMessage -> {
                    onShowMessage(it.message.second)
                }

                is UpdateExpenseUiEvent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }
    LaunchedEffect(expense) {
        if (expense != null) {
            viewModel.setInitialData(expense)
        }
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.Layout.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(PurpleGrey80.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.add_exp),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                )
            }

            StandardInputField(
                value = state.expTitle ?: "",
                onValueChange = {
                    viewModel.updateUiState(state.copy(expTitle = it))
                },
                label = "Expense Title", placeholder = "Gari vara",
                singleLine = true,
                leadingIcon = Icons.Default.Title
            )
            Spacer(Modifier.height(AppSpacing.Size.md))
            QuestionSelection(
                modifier = Modifier.padding(horizontal = AppSpacing.Size.md),
                options = state.expTypes,
                textValue = state.selectedExpType ?: "",
                onItemSelection = {
                    viewModel.updateUiState(
                        state.copy(
                            selectedExpType = it.name,
                            selectedExpTypeId = it.id
                        )
                    )
                },
                getLabel = { it.name },
                textLabel = "Expense Type",
            )
            Spacer(Modifier.height(AppSpacing.Size.sm))
            QuestionSelection(
                modifier = Modifier.padding(horizontal = AppSpacing.Size.md),
                options = state.expCategories,
                textValue = state.selectedExpCategory ?: "",
                onItemSelection = {
                    viewModel.updateUiState(
                        state.copy(
                            selectedExpCategory = it.name,
                            selectedExpCategoryId = it.id
                        )
                    )
                },
                getLabel = { it.name },
                textLabel = "Expense Category",
            )
            Spacer(Modifier.height(AppSpacing.Size.md))
            //var amount by remember { mutableStateOf(if (state.amount == 0.0) "" else state.amount.toString()) }

            StandardInputField(
                value = state.amount,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        viewModel.updateUiState(state.copy(amount = it))
                    }
                },
                label = "Amount(৳)", placeholder = "1xxxx",
                singleLine = true,
                leadingIcon = Icons.Default.CurrencyLira,
                keyboardType = KeyboardType.Number
            )
            Spacer(Modifier.height(AppSpacing.Size.md))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.Size.sm)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        viewModel.updateUiState(state.copy(showExpDatePicker = true))
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (state.selectedExpDate != 0L) {
                        state.selectedExpDate.longToSimpleDateFormatString()
                    } else {
                        "Expense Date"

                    },
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding((AppConstants.APP_MARGIN + 4).dp)
                )

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Expense Date",
                    modifier = Modifier.padding(end = AppConstants.APP_MARGIN.dp)
                )
            }
            Spacer(Modifier.height(AppSpacing.Size.xxl))
            if (!state.isLoading) {
                ElevatedButton(
                    onClick = {
                        viewModel.addExpense()
                    }, colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.Size.md)
                ) {
                    Text("Update")
                }

            }
        }
        if (state.showExpDatePicker) {
            CustomDatePickerDialog(
                confirmText = "Ok",
                dismissText = "Cancel", onDismiss = {
                    viewModel.updateUiState(
                        state.copy(showExpDatePicker = false)
                    )
                }, onConfirm = {
                    viewModel.updateUiState(
                        state.copy(
                            showExpDatePicker = false,
                            selectedExpDate = it
                        )
                    )
                })
        }
        if (state.isLoading) {
            AnimatedProgressDialog()
        }
    }
}

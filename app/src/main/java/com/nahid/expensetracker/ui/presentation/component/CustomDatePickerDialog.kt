package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.domain.model.toPickerMillis
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDateMillis: Long? = null,
    confirmText: String = "OK",
    dismissText: String = "Cancel",
    onDismiss: () -> Unit = {},
    onConfirm: (date: Long) -> Unit = {},
) {

    val today = LocalDate.now().toPickerMillis()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: today,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= today
            }
        }
    )
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.Size.sm),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(AppSpacing.Size.sm))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text(dismissText)
                    }
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let(onConfirm)
                    }) {
                        Text(confirmText)
                    }
                }
            }
        }
    }
}

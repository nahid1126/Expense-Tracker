package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties

@Preview
@Composable
fun ConfirmationDialog(
    title: String = "",
    message: String = "",
    confirmText: String = "Confirm",
    dismissText: String = "Dismiss",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {

    AlertDialog(
        containerColor = White,
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(confirmText, color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(dismissText, color = MaterialTheme.colorScheme.primary)
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = true),
    )

}
package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.nahid.expensetracker.core.AppSpacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> QuestionSelection(
    modifier: Modifier,
    options: List<T> = arrayListOf(),
    textValue: String?,
    textLabel: String,
    onItemSelection: (T) -> Unit = {},
    getLabel: (T) -> String = { it.toString() }
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = {
        isExpanded = !isExpanded
    }, modifier = modifier) {

        AssistChip(
            onClick = {
                isExpanded = true
            },
            label = {
                Text(
                    text = if (textValue.isNullOrEmpty()) textLabel else textValue,
                    modifier = Modifier.padding(vertical = AppSpacing.Layout.cardPadding),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = if (!textValue.isNullOrEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            trailingIcon = {
                Icon(
                    if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()

        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            },
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
        ) {
            options.forEach {
                DropdownMenuItem(text = {
                    Text(
                        text = getLabel(it),
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                }, onClick = {
                    isExpanded = false
                    onItemSelection(it)
                })
            }
        }
    }

}


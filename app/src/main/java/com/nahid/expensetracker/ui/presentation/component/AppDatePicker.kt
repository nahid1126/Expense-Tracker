@file:OptIn(ExperimentalMaterial3Api::class)

package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.nahid.expensetracker.core.AppSpacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun AppDatePicker(
    modifier: Modifier = Modifier,
    label: String = "Select Date",
    selectedDate: Long? = null,
    onDateSelected: (Long?, String) -> Unit,
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Set to end of day to include the current date in the range
                val endOfToday = calendar.timeInMillis + (24 * 60 * 60 * 1000) - 1

                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val sevenDaysAgo = calendar.timeInMillis

                return utcTimeMillis in sevenDaysAgo..endOfToday
            }
        }
    )

    val formattedDate = remember(
        datePickerState.selectedDateMillis
    ) {

        datePickerState.selectedDateMillis?.let {

            Instant
                .fromEpochMilliseconds(it)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .toString()

        } ?: ""
    }

    Column(
        modifier = modifier
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = CardDefaults.cardElevation(AppSpacing.Size.sm),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.Size.sm)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.Layout.cardPadding)
                    .clickable {
                        showDatePicker = true
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = formattedDate.ifEmpty { label },
                    style = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    modifier = Modifier
                )
            }
        }
    }

    if (showDatePicker) {

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        onDateSelected(
                            datePickerState.selectedDateMillis,
                            formattedDate
                        )

                        showDatePicker = false
                    }
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }

        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }
}
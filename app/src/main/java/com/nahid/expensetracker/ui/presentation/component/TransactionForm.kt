package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nahid.expensetracker.domain.model.toPickerMillis
import com.nahid.expensetracker.domain.model.transactionDateFromPicker
import com.nahid.expensetracker.domain.model.validTransactionAmount
import com.nahid.expensetracker.ui.theme.ExpenseTrackerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TransactionForm(
    title: String,
    amount: String,
    type: String?,
    category: String?,
    categories: List<String>,
    date: Long,
    isBusy: Boolean,
    saveLabel: String,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onSave: () -> Unit,
) {
    var submitted by rememberSaveable { mutableStateOf(false) }
    var categoryMenu by rememberSaveable { mutableStateOf(false) }
    var datePicker by rememberSaveable { mutableStateOf(false) }
    val invalidAmount = amount.validTransactionAmount() == null
    val missingType = type.isNullOrBlank()
    val missingCategory = category.isNullOrBlank()
    val missingDate = date == 0L
    val missingTitle = title.isBlank()
    val colors = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val categoryOptions = (categories + listOfNotNull(category)).distinct()

    Box(
        Modifier.fillMaxSize().background(colors.background).imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(Modifier.widthIn(max = 590.dp).fillMaxSize()) {
            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(start = 22.dp, end = 22.dp, top = 14.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    TransactionFilters(listOf("Expense", "Income"), type, onTypeChange)
                    if (submitted && missingType) FormError("Select a transaction type.")
                }

                AmountEditor(amount, onAmountChange, submitted && invalidAmount)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = colors.surfaceContainerLow,
                ) {
                    Column {
                        FormOption(
                            label = "Category",
                            value = category ?: if (isBusy) "Loading categories…" else "Choose a category",
                            icon = categoryIcon(category.orEmpty()),
                            enabled = categoryOptions.isNotEmpty(),
                            onClick = {
                                focusManager.clearFocus()
                                categoryMenu = true
                            },
                        )
                        if (submitted && missingCategory) FormError("Choose a category.", Modifier.padding(horizontal = 16.dp))
                        FormDivider()
                        FormOption(
                            label = "Date",
                            value = if (missingDate) "Choose a date" else transactionDateFromPicker(date)
                                .format(DateTimeFormatter.ofPattern("d MMM uuuu")),
                            icon = Icons.Default.CalendarMonth,
                            onClick = { datePicker = true },
                        )
                        if (submitted && missingDate) FormError("Choose a date.", Modifier.padding(horizontal = 16.dp))
                        FormDivider()
                        TextField(
                            value = title,
                            onValueChange = onTitleChange,
                            label = { Text("Title") },
                            placeholder = { Text("e.g. Groceries") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, null, Modifier.size(20.dp)) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                focusedLeadingIconColor = colors.primary,
                                unfocusedLeadingIconColor = colors.primary,
                            ),
                            isError = submitted && missingTitle,
                            supportingText = if (submitted && missingTitle) {
                                { Text("Enter a title.") }
                            } else null,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            HorizontalDivider(color = colors.outlineVariant)
            Button(
                onClick = {
                    submitted = true
                    if (!invalidAmount && !missingType && !missingCategory && !missingDate && !missingTitle) {
                        onSave()
                    }
                },
                enabled = !isBusy,
                shape = RoundedCornerShape(17.dp),
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth().heightIn(min = 54.dp),
            ) {
                Text(if (isBusy) "Please wait…" else saveLabel, style = MaterialTheme.typography.titleSmall)
            }
        }
    }

    if (categoryMenu) {
        CategoryPickerSheet(
            categories = categoryOptions,
            selected = category,
            onDismiss = { categoryMenu = false },
            onSelect = {
                onCategoryChange(it)
                categoryMenu = false
            },
        )
    }

    if (datePicker) {
        CustomDatePickerDialog(
            initialDateMillis = date.takeUnless { missingDate },
            onDismiss = { datePicker = false },
            onConfirm = {
                onDateChange(it)
                datePicker = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPickerSheet(
    categories: List<String>,
    selected: String?,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        CategoryPickerContent(categories, selected, onSelect)
    }
}

@Composable
private fun CategoryPickerContent(
    categories: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
) {
    var search by rememberSaveable { mutableStateOf("") }
    val colors = MaterialTheme.colorScheme
    val visible = categories.filter { it.contains(search.trim(), ignoreCase = true) }
    Column(
        Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Choose a category", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search categories") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        )
        if (visible.isEmpty()) {
            Text("No matching categories", color = colors.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp))
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(visible, key = { it }) { label ->
                    val checked = label == selected
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        color = if (checked) colors.primaryContainer else colors.surfaceContainer,
                        contentColor = if (checked) colors.onPrimaryContainer else colors.onSurface,
                    ) {
                        Row(
                            Modifier.selectable(checked, role = Role.RadioButton, onClick = { onSelect(label) })
                                .heightIn(min = 48.dp).padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(categoryIcon(label), contentDescription = null, modifier = Modifier.size(19.dp),
                                tint = if (checked) colors.onPrimaryContainer else colors.primary)
                            Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                            if (checked) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Category picker", widthDp = 393)
@Composable
private fun CategoryPickerPreview() {
    ExpenseTrackerTheme(darkTheme = false, dynamicColor = false) {
        Surface { CategoryPickerContent(listOf("Food", "Transport", "Shopping", "Salary"), "Food", {}) }
    }
}

@Composable
private fun AmountEditor(amount: String, onAmountChange: (String) -> Unit, hasError: Boolean) {
    val colors = MaterialTheme.colorScheme
    val amountStyle = MaterialTheme.typography.displayLarge.copy(
        color = colors.onPrimaryContainer,
        fontSize = if (amount.length > 7) 37.sp else 58.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-1.6).sp,
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.primaryContainer,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("Amount", color = colors.onPrimaryContainer, style = MaterialTheme.typography.labelLarge)
            BasicTextField(
                value = amount,
                onValueChange = { if (it.all(Char::isDigit)) onAmountChange(it) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp)
                    .semantics { contentDescription = "Amount in taka" },
                textStyle = amountStyle,
                cursorBrush = SolidColor(colors.onPrimaryContainer),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                decorationBox = { innerTextField ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "৳",
                            style = MaterialTheme.typography.headlineLarge,
                            color = colors.onPrimaryContainer,
                        )
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (amount.isEmpty()) {
                                Text("0", style = amountStyle, color = colors.onPrimaryContainer.copy(alpha = 0.55f))
                            }
                            innerTextField()
                        }
                    }
                },
            )
            if (hasError) FormError("Enter a whole taka amount from 1 to 2,147,483,647.")
        }
    }
}

@Composable
private fun FormOption(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    Surface(onClick = onClick, enabled = enabled, color = Color.Transparent) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, null, Modifier.size(20.dp), tint = colors.primary)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyLarge, color = colors.onSurface)
            }
            Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(18.dp), tint = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun FormDivider() {
    HorizontalDivider(Modifier.padding(start = 50.dp), color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun FormError(message: String, modifier: Modifier = Modifier) {
    Text(message, modifier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
}

@Preview(name = "Add transaction", widthDp = 393, heightDp = 680)
@Composable
private fun TransactionFormPreview() {
    ExpenseTrackerTheme(darkTheme = false, dynamicColor = false) {
        TransactionForm(
            title = "Groceries",
            amount = "2350",
            type = "Expense",
            category = "Food",
            categories = listOf("Food", "Transport"),
            date = LocalDate.of(2026, 11, 25).toPickerMillis(),
            isBusy = false,
            saveLabel = "Save transaction",
            onTitleChange = {},
            onAmountChange = {},
            onTypeChange = {},
            onCategoryChange = {},
            onDateChange = {},
            onSave = {},
        )
    }
}

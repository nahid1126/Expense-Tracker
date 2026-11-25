package com.nahid.expensetracker.ui.presentation.transections

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.asTaka
import com.nahid.expensetracker.domain.model.asTransactionDate
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.BarChart
import com.nahid.expensetracker.ui.presentation.component.CashFlowPeriod
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
import com.nahid.expensetracker.ui.presentation.component.SwipeToDeleteContainer
import com.nahid.expensetracker.ui.presentation.component.TransactionFilters
import com.nahid.expensetracker.ui.presentation.home.TransactionItem
import com.nahid.expensetracker.ui.theme.ExpenseTrackerTheme
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransectionsScreen(
    viewModel: TransectionsViewModel = koinViewModel(),
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit,
    onUpdate: (Expense) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler(onBack = onBack)
    LaunchedEffect(viewModel) {
        onChangeConfiguration(state.uiConfig)
        viewModel.uiMessage.collect { onShowMessage(it) }
    }

    TransactionHistory(
        state,
        onTypeSelect = viewModel::selectType,
        onMonthSelect = viewModel::selectMonth,
        onUpdate = onUpdate,
        onDelete = viewModel::requestDelete,
    )
    state.pendingDelete?.let { expense ->
        ConfirmationDialog(
            title = "Delete transaction?",
            message = "Remove ${expense.title} from your transactions?",
            confirmText = "Delete",
            dismissText = "Cancel",
            onDismiss = { viewModel.requestDelete(null) },
            onConfirm = viewModel::deleteExpense,
        )
    }
}

@Composable
private fun TransactionHistory(
    state: TransectionsUiState,
    onTypeSelect: (String?) -> Unit,
    onMonthSelect: (YearMonth?) -> Unit,
    onUpdate: (Expense) -> Unit,
    onDelete: (Expense) -> Unit,
    initiallyShowInsights: Boolean = true,
) {
    var showInsights by rememberSaveable { mutableStateOf(initiallyShowInsights) }
    val byDay = remember(state.transactions) {
        state.transactions.groupBy { it.date.asTransactionDate()?.toString() ?: it.date }
    }

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 590.dp).fillMaxSize(),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 16.dp),
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MonthFilter(state.month, state.months, onMonthSelect, Modifier.weight(1f, fill = false))
                    TextButton(onClick = { showInsights = !showInsights }, enabled = state.transactions.isNotEmpty()) {
                        Icon(Icons.Default.BarChart, null, Modifier.size(18.dp))
                        Text("Insights", Modifier.padding(horizontal = 5.dp))
                        Icon(
                            if (showInsights) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (showInsights) "Collapse" else "Expand",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
            item {
                Box(Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                    TransactionFilters(listOf("All", "Expense", "Income"), state.type ?: "All") {
                        onTypeSelect(it.takeUnless { option -> option == "All" })
                    }
                }
            }
            if (showInsights && state.transactions.isNotEmpty()) {
                item { HistoryInsights(state) }
            }
            if (byDay.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(top = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ReceiptLong,
                            null,
                            Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text("No transactions", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Choose another month or filter.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            for ((day, expenses) in byDay) {
                item(key = "date:$day") {
                    Text(
                        dateHeading(day),
                        Modifier.padding(top = 18.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(expenses, key = { requireNotNull(it.id) }) { expense ->
                    Box(Modifier.padding(top = 7.dp).semantics {
                        customActions = listOf(CustomAccessibilityAction("Delete transaction") {
                            onDelete(expense)
                            true
                        })
                    }) {
                        SwipeToDeleteContainer(item = expense, onDelete = onDelete) {
                            TransactionItem(it, onClick = onUpdate, showDate = false)
                        }
                    }
                }
            }
        }
    }
}

private fun dateHeading(day: String): String {
    val date = day.asTransactionDate() ?: return day
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, d MMM uuuu"))
    }
}

@Composable
private fun MonthFilter(
    selectedMonth: YearMonth?,
    months: List<YearMonth>,
    onSelect: (YearMonth?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("MMM uuuu")
    val colors = MaterialTheme.colorScheme
    Box(modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(16.dp),
            color = colors.surfaceContainerLow,
        ) {
            Row(
                Modifier.heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.CalendarMonth, null, Modifier.size(18.dp), tint = colors.primary)
                Text(selectedMonth?.format(formatter) ?: "All time", style = MaterialTheme.typography.labelLarge)
                Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(17.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("All time") }, onClick = {
                onSelect(null)
                expanded = false
            })
            for (month in months) {
                DropdownMenuItem(text = { Text(month.format(formatter)) }, onClick = {
                    onSelect(month)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun HistoryInsights(state: TransectionsUiState) {
    var showTimeline by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            TransactionFilters(
                options = listOf("Categories", "Timeline"),
                selected = if (showTimeline) "Timeline" else "Categories",
                onSelect = { showTimeline = it == "Timeline" },
            )
            if (showTimeline) CashFlowTimeline(state) else CategoryReport(state)
        }
    }
}

@Composable
private fun CashFlowTimeline(state: TransectionsUiState) {
    val monthly = state.month == null
    val periods = remember(state.transactions, monthly) {
        val labels = DateTimeFormatter.ofPattern(if (monthly) "MMM yy" else "d")
        val dates = DateTimeFormatter.ofPattern(if (monthly) "MMMM uuuu" else "d MMM uuuu")
        state.transactions.mapNotNull { expense ->
            val date = expense.date.asTransactionDate() ?: return@mapNotNull null
            (if (monthly) date.withDayOfMonth(1) else date) to expense
        }.groupBy({ it.first }, { it.second }).toSortedMap().map { (date, entries) ->
            CashFlowPeriod(
                label = date.format(labels),
                description = date.format(dates),
                income = entries.filter { it.type == "Income" }.sumOf { it.amount.toLong() },
                expense = entries.filter { it.type == "Expense" }.sumOf { it.amount.toLong() },
            )
        }
    }
    Text(
        if (monthly) "Monthly cash flow" else "Daily cash flow",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    )
    if (periods.isEmpty()) {
        Text(
            "No dated transactions to chart.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        BarChart(
            periods,
            showIncome = state.type != "Expense",
            showExpense = state.type != "Income",
        )
    }
}

private data class ReportCategory(val name: String, val amount: Long)

private val reportColors = listOf(
    Color(0xFF7957D5),
    Color(0xFF2789AA),
    Color(0xFFDD9261),
    Color(0xFF39A185),
    Color(0xFFCB719E),
    Color(0xFF8093C8),
)

@Composable
private fun CategoryReport(state: TransectionsUiState) {
    val categories = remember(state.transactions) {
        val ranked = state.transactions.filter { it.amount > 0 }.groupBy {
            it.category.trim().ifBlank { "Uncategorized" }.lowercase(Locale.ROOT)
        }.values.map { rows ->
            ReportCategory(rows.first().category.trim().ifBlank { "Uncategorized" }, rows.sumOf { it.amount.toLong() })
        }.sortedWith(compareByDescending<ReportCategory> { it.amount }.thenBy { it.name })
        if (ranked.size <= reportColors.size) ranked
        else ranked.take(reportColors.size - 1) +
            ReportCategory("Other categories", ranked.drop(reportColors.size - 1).sumOf { it.amount })
    }
    val total = categories.sumOf { it.amount }
    if (total == 0L) {
        Text("No amounts to chart.", style = MaterialTheme.typography.bodyMedium)
        return
    }
    val colors = MaterialTheme.colorScheme
    val caption = when (state.type) {
        "Income" -> "Total income"
        "Expense" -> "Total expenses"
        else -> "Income + expenses"
    }
    val percent = remember { NumberFormat.getPercentInstance() }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            Modifier.align(Alignment.CenterHorizontally).size(216.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.matchParentSize()) {
                val strokeWidth = 18.dp.toPx()
                val inset = strokeWidth / 2
                var angle = -90f
                categories.forEachIndexed { index, category ->
                    val sweep = (category.amount.toDouble() / total * 360).toFloat()
                    val gap = if (categories.size == 1) 0f else minOf(5f, sweep / 4)
                    drawArc(
                        color = reportColors[index],
                        startAngle = angle + gap / 2,
                        sweepAngle = sweep - gap,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(strokeWidth),
                    )
                    angle += sweep
                }
            }
            Column(
                Modifier.fillMaxWidth(0.70f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(caption, style = MaterialTheme.typography.labelMedium, color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center)
                Text(
                    total.asTaka(),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = 26.sp),
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
        categories.forEachIndexed { index, category ->
            val share = category.amount.toDouble() / total
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(9.dp).background(reportColors[index], CircleShape))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(category.name, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        Text(category.amount.asTaka(), style = MaterialTheme.typography.labelLarge)
                    }
                    LinearProgressIndicator(
                        progress = { share.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = reportColors[index],
                        trackColor = colors.surfaceContainer,
                        gapSize = 0.dp,
                        drawStopIndicator = {},
                    )
                }
                Text(percent.format(share), style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            }
        }
    }
}

@Preview(name = "Transactions and Insights", widthDp = 393, heightDp = 790)
@Composable
private fun TransactionHistoryPreview() {
    ExpenseTrackerTheme(darkTheme = false, dynamicColor = false) {
        val items = listOf(
            Expense(id = 1, title = "Groceries", amount = 2350, date = "2026-11-25", type = "Expense", category = "Food", isSynced = true),
            Expense(id = 2, title = "Freelance payment", amount = 5400, date = "2026-11-25", type = "Income", category = "Salary", isSynced = true),
            Expense(id = 3, title = "Metro pass", amount = 1600, date = "2026-11-24", type = "Expense", category = "Transport", isSynced = true),
            Expense(id = 4, title = "Groceries", amount = 3000, date = "2026-11-23", type = "Expense", category = "Food", isSynced = true),
            Expense(id = 5, title = "Freelance payment", amount = 4250, date = "2026-11-22", type = "Income", category = "Salary", isSynced = true),
            Expense(id = 6, title = "Books", amount = 1900, date = "2026-11-22", type = "Expense", category = "Shopping", isSynced = true),
        )
        TransactionHistory(
            TransectionsUiState(month = YearMonth.of(2026, 11), transactions = items),
            onTypeSelect = {},
            onMonthSelect = {},
            onUpdate = {},
            onDelete = {},
            initiallyShowInsights = true,
        )
    }
}

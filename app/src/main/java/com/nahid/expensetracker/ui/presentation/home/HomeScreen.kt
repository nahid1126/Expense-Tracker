package com.nahid.expensetracker.ui.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.asTaka
import com.nahid.expensetracker.domain.model.asTransactionDate
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
import com.nahid.expensetracker.ui.presentation.component.categoryIcon
import com.nahid.expensetracker.ui.theme.ExpenseTrackerTheme
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onChangeConfiguration: (MainUIConfig) -> Unit,
    toExit: () -> Unit = {},
    onUpdate: (Expense) -> Unit,
    onSeeAllClicked: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler { viewModel.showExitDialog(true) }
    LaunchedEffect(viewModel) {
        onChangeConfiguration(state.uiConfig)
        viewModel.getInitialData()
    }

    Dashboard(state, onUpdate, onSeeAllClicked)

    if (state.showExitDialog) {
        ConfirmationDialog(
            title = "Exit Expense Tracker?",
            message = "Your saved transactions will be here when you return.",
            confirmText = "Exit",
            dismissText = "Cancel",
            onDismiss = { viewModel.showExitDialog(false) },
            onConfirm = {
                viewModel.showExitDialog(false)
                toExit()
            },
        )
    }
}

@Composable
private fun Dashboard(
    state: HomeUiState,
    onUpdate: (Expense) -> Unit,
    onSeeAllClicked: () -> Unit,
) {
    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 590.dp).fillMaxSize(),
            contentPadding = PaddingValues(start = 22.dp, end = 22.dp, top = 18.dp, bottom = 16.dp),
        ) {
            item { BalanceSummary(state, onSeeAllClicked) }
            item {
                Column(Modifier.padding(top = 24.dp)) {
                    SpendingBreakdown(state.spending)
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Recent transactions", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onSeeAllClicked) {
                        Text("See all")
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 6.dp).size(16.dp),
                        )
                    }
                }
            }
            if (state.recentTransactions.isEmpty()) {
                item {
                    Text(
                        "Your latest transactions will appear here.",
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            items(state.recentTransactions, key = { requireNotNull(it.id) }) { expense ->
                Box(Modifier.padding(top = 8.dp)) {
                    TransactionItem(expense, onClick = onUpdate)
                }
            }
        }
    }
}

@Composable
private fun BalanceSummary(state: HomeUiState, onReportClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(28.dp)
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            Modifier.fillMaxWidth().clip(shape).background(
                Brush.linearGradient(listOf(Color(0xFF123B2D), Color(0xFF18543D), Color(0xFF267150))),
            ),
        ) {
            Canvas(Modifier.matchParentSize()) {
                val center = Offset(size.width * 0.96f, size.height * 0.14f)
                for (fraction in listOf(0.30f, 0.39f, 0.48f)) {
                    drawCircle(Color.White.copy(alpha = 0.12f), size.width * fraction,
                        center, style = Stroke(width = 1.dp.toPx()))
                }
            }
            Column(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Total balance", color = Color(0xFFE5F5E9), style = MaterialTheme.typography.titleSmall)
                Text(
                    text = state.totalBalance.asTaka(),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(minFontSize = 16.sp, maxFontSize = 46.sp),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                )
                Surface(
                    onClick = onReportClick,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f),
                    contentColor = Color.White,
                ) {
                    Row(
                        Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("View report", style = MaterialTheme.typography.labelLarge)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(15.dp))
                    }
                }
            }
        }
        Text("Money overview", style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BalanceMetric("Income", state.totalIncome, Icons.Default.ArrowDownward, Modifier.weight(1f), isIncome = true)
            BalanceMetric("Expenses", state.totalExpense, Icons.Default.ArrowUpward, Modifier.weight(1f), isIncome = false)
        }
    }
}

@Composable
private fun BalanceMetric(
    label: String,
    amount: Long,
    icon: ImageVector,
    modifier: Modifier,
    isIncome: Boolean,
) {
    val colors = MaterialTheme.colorScheme
    val labelColor = if (isIncome) colors.onPrimaryContainer else colors.onTertiaryContainer
    Surface(
        modifier = modifier,
        color = colors.surfaceContainerLow,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, colors.outlineVariant),
        contentColor = colors.onSurface,
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = CircleShape,
                    color = if (isIncome) colors.primaryContainer else colors.tertiaryContainer,
                    contentColor = labelColor,
                ) {
                    Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
                    }
                }
                Text(label, style = MaterialTheme.typography.labelLarge)
            }
            Text(
                amount.asTaka(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

data class CategorySpending(val label: String, val amount: Long)

internal fun spendingBreakdown(expenses: List<Expense>): List<CategorySpending> {
    val categories = expenses.filter { it.type == "Expense" && it.amount > 0 }
        .groupBy { it.category.trim().ifBlank { "Uncategorized" }.lowercase(Locale.ROOT) }
        .map { (_, transactions) ->
            CategorySpending(
                transactions.first().category.trim().ifBlank { "Uncategorized" },
                transactions.sumOf { it.amount.toLong() },
            )
        }.sortedWith(compareByDescending<CategorySpending> { it.amount }.thenBy { it.label })
    return if (categories.size <= 6) categories else categories.take(5) +
        CategorySpending("Other categories", categories.drop(5).sumOf { it.amount })
}

private val categoryAccents = listOf(
    Color(0xFF278F71), Color(0xFF3886C6), Color(0xFFC27830),
    Color(0xFF8A64C9), Color(0xFFC45982), Color(0xFF679245),
)

@Composable
private fun SpendingBreakdown(categories: List<CategorySpending>) {
    val colors = MaterialTheme.colorScheme
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Spending by category", Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
            Text("All time", color = colors.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
        }
        if (categories.isEmpty()) {
            Text(
                "Add an expense to see your breakdown.",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            return
        }

        val total = categories.sumOf { it.amount }
        val visibleCategories = if (expanded) categories else categories.take(3)
        val percentages = remember { NumberFormat.getPercentInstance() }
        Surface(shape = RoundedCornerShape(22.dp), color = colors.surfaceContainerLow) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                visibleCategories.forEachIndexed { index, category ->
                    val accent = categoryAccents[index % categoryAccents.size]
                    val share = (category.amount.toDouble() / total).toFloat()
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                Modifier.size(32.dp).background(
                                    lerp(colors.surfaceContainerLow, accent, 0.18f),
                                    RoundedCornerShape(10.dp),
                                ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(categoryIcon(category.label), null, Modifier.size(17.dp), tint = colors.onSurface)
                            }
                            Text(category.label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            Column(horizontalAlignment = Alignment.End) {
                                Text(category.amount.asTaka(), style = MaterialTheme.typography.labelLarge)
                                Text(percentages.format(share), style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant)
                            }
                        }
                        LinearProgressIndicator(
                            progress = { share },
                            modifier = Modifier.fillMaxWidth().height(5.dp),
                            color = accent,
                            trackColor = lerp(colors.surfaceContainerLow, accent, 0.13f),
                            gapSize = 0.dp,
                            drawStopIndicator = {},
                        )
                    }
                }
            }
        }
        if (categories.size > 3) {
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.Start),
                contentPadding = PaddingValues(horizontal = 0.dp),
            ) {
                Text(if (expanded) "Show fewer categories" else "Show more categories")
            }
        }
    }
}

@Composable
internal fun TransactionItem(
    expense: Expense,
    onClick: (Expense) -> Unit,
    showDate: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    val income = expense.type == "Income"
    Surface(
        onClick = { onClick(expense) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceContainerLow,
        border = BorderStroke(1.dp, colors.outlineVariant),
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val iconBackground = if (income) colors.primaryContainer else colors.tertiaryContainer
            val iconColor = if (income) colors.onPrimaryContainer else colors.onTertiaryContainer
            Surface(shape = CircleShape, color = iconBackground, contentColor = iconColor) {
                Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    Icon(categoryIcon(expense.category), contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    expense.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    if (expense.isSynced) expense.category else "${expense.category} · Pending sync",
                    color = colors.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                val sign = if (income) "+" else "−"
                Text(
                    sign + expense.amount.toLong().asTaka(),
                    color = if (income) colors.primary else colors.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                )
                if (showDate) {
                    Text(
                        expense.date.asTransactionDate()?.format(DateTimeFormatter.ofPattern("d MMM uuuu")) ?: expense.date,
                        color = colors.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Preview(name = "Dashboard", widthDp = 393, heightDp = 750)
@Composable
private fun DashboardPreview() {
    val expenses = listOf(
        Expense(id = 1, title = "Groceries", amount = 2350, date = "2026-11-25", type = "Expense", category = "Food", isSynced = true),
        Expense(id = 2, title = "Metro pass", amount = 1600, date = "2026-11-24", type = "Expense", category = "Transport", isSynced = true),
        Expense(id = 3, title = "Bookshop", amount = 840, date = "2026-11-23", type = "Expense", category = "Shopping", isSynced = true),
    )
    ExpenseTrackerTheme(darkTheme = false, dynamicColor = false) {
        Dashboard(
            HomeUiState(totalIncome = 42000, totalExpense = 4790, totalBalance = 37210,
                spending = spendingBreakdown(expenses), recentTransactions = expenses),
            onUpdate = {},
            onSeeAllClicked = {},
        )
    }
}

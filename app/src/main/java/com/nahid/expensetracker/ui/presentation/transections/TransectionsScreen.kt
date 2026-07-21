package com.nahid.expensetracker.ui.presentation.transections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.core.utils.extension.onSingleClick
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.model.NavigationButton
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.presentation.component.BarChart
import com.nahid.expensetracker.ui.presentation.component.BarChartData
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
import com.nahid.expensetracker.ui.presentation.home.TransactionList
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TransectionsScreen(
    viewModel: TransectionsViewModel = koinViewModel(),
    onChangeConfiguration: (MainUIConfig) -> Unit,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit,
    onUpdate: (Expense) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var startAnimation by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        onChangeConfiguration(state.uiConfig)
        delay(200)
        startAnimation = true
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is TransectionsUiEvent.ShowMessage -> {
                    onShowMessage(it.message.second)
                }
                is TransectionsUiEvent.NavigateBack -> {
                    onBack()
                }
            }
        }
    }


    LaunchedEffect(selectedTab) {
        val tabName = if (selectedTab == 0) "Income" else "Expense"
        viewModel.updateUiState(state.copy(selectedTab = tabName))
    }

    val tabs = listOf(
        NavigationButton("Income", Icons.Default.AccountBalanceWallet),
        NavigationButton("Expense", Icons.Default.Payments),
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Modern Segmented Tab Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppSpacing.Layout.screenPadding,
                        vertical = AppSpacing.Size.md
                    ),
                shape = RoundedCornerShape(AppSpacing.Size.md),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTab == index
                        val backgroundColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            animationSpec = tween(durationMillis = 300),
                            label = "tabBackground"
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "tabContent"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(AppSpacing.Size.sm))
                                .background(backgroundColor)
                                .onSingleClick { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = contentColor
                                )
                                Spacer(Modifier.width(AppSpacing.Size.sm))
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = contentColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

            }
            FinalExpenseGraphCard(
                expenses = state.incomeExpenseList,
                isIncome = selectedTab == 0
            )

            // Screen Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.Layout.screenPadding)
            ) {
                if (startAnimation) {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { it } + fadeIn(tween(600)))
                                    .togetherWith(slideOutHorizontally { -it } + fadeOut(tween(600)))
                            } else {
                                (slideInHorizontally { -it } + fadeIn(tween(600)))
                                    .togetherWith(slideOutHorizontally { it } + fadeOut(tween(600)))
                            }
                        },
                        label = "tabTransition"
                    ) { _ ->
                        if (state.incomeExpenseList.isNotEmpty()) {
                            TransactionList(
                                list = state.incomeExpenseList,
                                onDelete = {
                                    viewModel.updateUiState(
                                        state.copy(
                                            showExpenseDeleteDialog = true,
                                            deleteExpenseItem = it
                                        )
                                    )
                                },
                                onItemClick = { onUpdate(it) }
                            )
                        }
                    }
                }
            }
        }
        if (state.showExpenseDeleteDialog) {
            ConfirmationDialog(
                title = "Warning !",
                message = "Are You Sure Want to Delete This Item ?",
                confirmText = "OK",
                dismissText = "Cancel",
                onDismiss = {
                    viewModel.updateUiState(
                        state.copy(
                            showExpenseDeleteDialog = false,
                            deleteExpenseItem = null
                        )
                    )
                },
                onConfirm = {
                    if (state.deleteExpenseItem != null) {
                        viewModel.deleteExpense()
                    }
                    viewModel.updateUiState(state.copy(showExpenseDeleteDialog = false))
                }
            )
        }
        if (state.isLoading) {
            AnimatedProgressDialog()
        }
    }
}

@Composable
fun FinalExpenseGraphCard(expenses: List<Expense>, isIncome: Boolean) {
    val monthMap = mapOf(
        "Jan" to "January", "Feb" to "February", "Mar" to "March", "Apr" to "April",
        "May" to "May", "Jun" to "June", "Jul" to "July", "Aug" to "August",
        "Sep" to "September", "Oct" to "October", "Nov" to "November", "Dec" to "December"
    )

    val availableMonths = remember {
        val monthFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        (0..11).map { i ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.MONTH, i)
            monthFormat.format(cal.time)
        }
    }

    val initialMonth = remember(availableMonths) {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val currentMonth = monthFormat.format(calendar.time)

        calendar.add(Calendar.MONTH, -1)
        val previousMonth = monthFormat.format(calendar.time)

        when {
            availableMonths.contains(currentMonth) -> currentMonth
            availableMonths.contains(previousMonth) -> previousMonth
            else -> availableMonths.firstOrNull() ?: currentMonth
        }
    }

    var selectedMonth by remember { mutableStateOf("") }

    LaunchedEffect(initialMonth) {
        selectedMonth = initialMonth
    }

    var expanded by remember { mutableStateOf(false) }

    val graphData = remember(selectedMonth, expenses) {
        if (selectedMonth.isEmpty()) return@remember emptyList()
        expenses.asSequence().filter {
            val part = it.date.split("-").getOrNull(1) ?: ""
            val fullName = monthMap[part] ?: part
            fullName == selectedMonth
        }
            .groupBy { it.date.split("-").getOrNull(0) ?: "" }
            .map { (day, list) ->
                BarChartData(day, list.sumOf { it.amount }.toFloat())
            }
            .sortedBy { it.label }
            .take(8).toList()
    }

    if (graphData.isEmpty() && selectedMonth.isNotEmpty()) {
        // We handle this inside the Card UI below
    }

    val totalAmount = remember(graphData) {
        graphData.sumOf { it.value.toDouble() }
    }

    val dateRangeText = remember(selectedMonth, expenses) {
        val filtered = expenses.filter {
            val part = it.date.split("-").getOrNull(1) ?: ""
            val fullName = monthMap[part] ?: part
            fullName == selectedMonth
        }
        if (filtered.isEmpty()) "No transactions in $selectedMonth"
        else {
            val sorted = filtered.sortedBy { it.date }
            "${sorted.first().date} - ${sorted.last().date}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (availableMonths.isNotEmpty()) {
            Box {
                Row(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Month: $selectedMonth",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableMonths.forEach { month ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = month,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = {
                                selectedMonth = month
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (graphData.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions found for $selectedMonth",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Text(
                        text = dateRangeText,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "৳ ${
                            String.format(
                                LocalLocale.current.platformLocale,
                                "%.2f",
                                totalAmount
                            )
                        }",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BarChart(
                        data = graphData,
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary
                        ),
                        isIncome = isIncome,
                        currencySymbol = "৳",
                        showYAxis = true,
                        showPercentBarTop = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }
    }
}
package com.nahid.expensetracker.ui.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.domain.model.Expense
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
import com.nahid.expensetracker.ui.theme.Black
import com.nahid.expensetracker.ui.theme.DarkGreen
import com.nahid.expensetracker.ui.theme.Gray
import com.nahid.expensetracker.ui.theme.RedFox
import com.nahid.expensetracker.ui.theme.Typography
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "HomeScreen"
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onChangeConfiguration: (MainUIConfig) -> Unit,
    user: String,
    onShowMessage: (String) -> Unit,
    toExit: () -> Unit = {},
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.updateUiState(state.copy(showExitDialog = true))
    }
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onChangeConfiguration(
            state.uiConfig
        )
        delay(200)
        startAnimation = true
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is HomeUiEvent.ShowMessage -> {
                    onShowMessage(it.message.second)
                }
            }
        }
    }
    @Composable
    fun dashboardEntrance(index: Int): Modifier {
        val alpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = index * 100,
                easing = FastOutSlowInEasing
            ),
            label = "alpha_$index"
        )
        val translationY by animateFloatAsState(
            targetValue = if (startAnimation) 0f else 40f,
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = index * 100,
                easing = FastOutSlowInEasing
            ),
            label = "translationY_$index"
        )
        return Modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = translationY
        }
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.Layout.screenPadding),
        ) {
            BalanceCardView(dashboardEntrance(3), state)
            Spacer(Modifier.height(AppSpacing.Size.md))
            Row(
                modifier = dashboardEntrance(2)
                    .fillMaxWidth()
                    .padding(AppSpacing.Size.md),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Transections", style = Typography.titleMedium.copy(
                        color = Black,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    "See All", style = Typography.bodyLarge.copy(
                        color = Gray,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            Spacer(Modifier.height(AppSpacing.Size.md))
            AnimatedVisibility(
                visible = startAnimation,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(600)
                ) + fadeIn(),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(600)
                ) + fadeOut()
            ) {
                TransactionList(list = state.topExpenseEntityList)
            }
        }

        if (state.showExitDialog) {
            ConfirmationDialog(
                title = "Exit !",
                message = "Are You Sure Want to Exit ?",
                confirmText = "Exit",
                dismissText = "Cancel",
                onDismiss = {
                    viewModel.updateUiState(state.copy(showExitDialog = false))
                },
                onConfirm = {
                    toExit()
                    viewModel.updateUiState(state.copy(showExitDialog = false))
                }
            )
        }

        if (state.isLoading) {
            AnimatedProgressDialog()
        }
    }
}

@Composable
fun BalanceCardView(modifier: Modifier, state: HomeUiState) {
    val gradientColors = listOf(
        Color(0xFF2B5748),
        Color(0xFF9CB080),
        Color(0xFF2B5748)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = AppSpacing.Size.sm)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(colors = gradientColors))
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Balance",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = modifier.height(4.dp))
                Text(
                    text = "৳${state.totalBalance}",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RowItem("Income", state.totalIncome.toString(),Icons.Default.ArrowDownward, DarkGreen,modifier)
                RowItem("Expenses", state.totalExpense.toString(),Icons.Default.ArrowUpward, RedFox,modifier)
            }
        }
    }
}

@Composable
fun RowItem(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = modifier.size(18.dp)
            )
        }
        Spacer(modifier = modifier.width(8.dp))
        Column {
            Text(text = title, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text(
                text = "৳$value",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TransactionList(list: List<Expense>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Size.sm)
    ) {
        items(list) { expense ->
            TransactionItem(expense = expense)
        }
    }
}

@Composable
fun TransactionItem(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = getCategoryGradient(expense.category),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(expense.category),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (expense.type == "Expense") "-" else "+"
                val color = if (expense.type == "Expense") Color.Red else DarkGreen

                Text(
                    text = "$prefix৳${expense.amount}.00",
                    style = Typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                Text(
                    text = formatDateLabel(expense.date),
                    style = Typography.bodySmall.copy(
                        color = Gray,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
        }
    }
}

private fun getCategoryGradient(category: String): Brush {
    return when (category.lowercase()) {
        "food" -> Brush.verticalGradient(colors = listOf(Color(0xFFFDC830), Color(0xFFF37335)))
        "shopping" -> Brush.verticalGradient(colors = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
        "entertainment" -> Brush.verticalGradient(colors = listOf(Color(0xFFF85032), Color(0xFFE73827)))
        "transport", "transportation" -> Brush.verticalGradient(colors = listOf(Color(0xFF00c6ff), Color(0xFF0072ff)))
        "home", "home rent" -> Brush.verticalGradient(colors = listOf(Color(0xFF2B5748), Color(0xFF9CB080)))
        "health", "medical" -> Brush.verticalGradient(colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)))
        "salary", "income" -> Brush.verticalGradient(colors = listOf(Color(0xFF11998e), Color(0xFF38ef7d)))
        "deposit" -> Brush.verticalGradient(colors = listOf(Color(0xFF3F51B5), Color(0xFF009688)))
        else -> Brush.verticalGradient(colors = listOf(Color(0xFFBDBDBD), Color(0xFF757575)))
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "home", "home rent" -> Icons.Default.Home
        "food" -> Icons.Default.Restaurant
        "transport", "transportation" -> Icons.Default.DirectionsBus
        "shopping" -> Icons.Default.ShoppingBag
        "salary" -> Icons.Default.Payments
        "health" -> Icons.Default.MedicalServices
        "education" -> Icons.Default.School
        "entertainment" -> Icons.Default.ConfirmationNumber
        "deposit" -> Icons.Default.AddCircle
        else -> Icons.Default.Category
    }
}

private fun formatDateLabel(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: return dateStr
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time

        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        when (fmt.format(date)) {
            fmt.format(today) -> "Today"
            fmt.format(yesterday) -> "Yesterday"
            else -> dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}

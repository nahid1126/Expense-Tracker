package com.nahid.expensetracker.ui.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.core.utils.Utils
import com.nahid.expensetracker.data.local.entity.Expense
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
import com.nahid.expensetracker.ui.theme.DarkGreen
import com.nahid.expensetracker.ui.theme.RedFox
import com.nahid.expensetracker.ui.theme.Zinc
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

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
            BalanceCardView()
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
fun BalanceCardView() {
    val gradientColors = listOf(
        Color(0xFF2B5748),
        Color(0xFF9CB080),
        Color(0xFF2B5748)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(colors = gradientColors))
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Balance",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "4800.00৳",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RowItem("Income", "2.500.00",Icons.Default.ArrowDownward, DarkGreen)
                RowItem("Expenses", "800.00",Icons.Default.ArrowUpward, RedFox)
            }
        }
    }
}

@Composable
fun RowItem(title: String, value: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text(
                text = "${value}৳",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TransactionList(
    modifier: Modifier,
    list: List<Expense>,
    title: String,
) {
    LazyColumn(modifier = modifier) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(text = title, fontSize = 20.sp)
                Text(
                    text = "See All",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 55.dp)
                )
            }
        }
        items(list, key = { it.id!! }) {
            TransactionListItem(
                title = it.title,
                amount = "৳${it.amount}",
                icon = Utils.getIcon(it),
                date = it.date,
                color = if (it.type == "Income") Color.Green else Color.Red, it.id
            )
        }
    }
}

@Composable
fun TransactionListItem(
    title: String,
    amount: String,
    icon: Int,
    date: String,
    color: Color,
    id: Int?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 55.dp, bottom = 8.dp)
            .clickable { }
    ) {
        Row {
            Image(
                painter = painterResource(id = icon), contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = CircleShape)
                    .border(2.dp, Zinc, CircleShape)
                    .background(Color.Gray)
                    .scale(.6f)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                Text(text = title, fontSize = 16.sp)
                Text(text = date, fontSize = 12.sp)
            }
        }
        Text(
            text = amount,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = color
        )
    }
}
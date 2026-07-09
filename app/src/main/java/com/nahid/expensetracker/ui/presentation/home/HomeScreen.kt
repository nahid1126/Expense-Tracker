package com.nahid.expensetracker.ui.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nahid.expensetracker.R
import com.nahid.expensetracker.core.AppSpacing
import com.nahid.expensetracker.core.utils.Utils
import com.nahid.expensetracker.data.local.entity.Expense
import com.nahid.expensetracker.domain.model.User
import com.nahid.expensetracker.domain.uiconfig.MainUIConfig
import com.nahid.expensetracker.ui.presentation.component.AnimatedProgressDialog
import com.nahid.expensetracker.ui.presentation.component.ConfirmationDialog
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
            CardItem(modifier = dashboardEntrance(3),"123548","4520","187965412")
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
fun CardItem(modifier: Modifier, totalBalance: String, expanse: String, income: String) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Zinc)
            .padding(16.dp)

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(text = "Total Balance", fontSize = 16.sp, color = Color.White)
                Text(
                    text = totalBalance,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Image(
                painter = painterResource(
                    id = R.drawable.ic_dot
                ), contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CardRowItem(
                modifier = Modifier.align(Alignment.CenterStart),
                title = "Income", amount = income,
                icon = R.drawable.ic_down
            )
            CardRowItem(
                modifier = Modifier.align(Alignment.CenterEnd),
                title = "Expense", amount = expanse,
                icon = R.drawable.ic_up
            )
        }
    }
}

@Composable
fun CardRowItem(modifier: Modifier, title: String, amount: String, icon: Int) {
    Column(modifier = modifier) {
        Row {
            Image(painter = painterResource(id = icon), contentDescription = null)
            Text(text = title, fontSize = 16.sp, color = Color.White)
        }
        Text(text = amount, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
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
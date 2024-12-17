package com.nahid.expensetracker.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nahid.expensetracker.R
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.ui.theme.Zinc
import com.nahid.expensetracker.view_model.HomeViewModel
import com.nahid.expensetracker.view_model.HomeViewModelFactory

private const val TAG = "HomeScreen"
@Composable
fun HomeScreen(rememberNavController: NavHostController) {
    val viewModel: HomeViewModel =
        HomeViewModelFactory(LocalContext.current).create(HomeViewModel::class.java)
    val expenseList by viewModel.expenseList.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, card, topBar, actionButton) = createRefs()
            Image(painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                .constrainAs(nameRow) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                Column {
                    Text(text = "Good Afternoon", fontSize = 16.sp, color = Color.White)
                    Text(
                        text = "This is a simple app",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            val totalBalance = viewModel.getBalance()
            val expanse = viewModel.getExpense()
            val income = viewModel.getIncome()

            CardItem(modifier = Modifier.constrainAs(card) {
                top.linkTo(nameRow.bottom)
                start.linkTo(nameRow.start)
                end.linkTo(nameRow.end)
            }, totalBalance, expanse, income)
            TransactionList(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .constrainAs(list) {
                        top.linkTo(card.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    }, list = expenseList, viewModel = viewModel
            )
            FloatingActionButton(
                containerColor = Zinc,
                contentColor = Color.White,
                onClick = { rememberNavController.navigate("/add") },
                modifier = Modifier
                    .zIndex(1f)
                    .padding(10.dp)
                    .constrainAs(actionButton) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    }
}

@Composable
fun CardItem(modifier: Modifier, totalBalance: String, expanse: String, income: String) {
    Column(
        modifier = modifier
            .padding(top = 60.dp, end = 16.dp, start = 16.dp, bottom = 16.dp)
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
fun TransactionList(modifier: Modifier, list: List<Expense>, viewModel: HomeViewModel) {
    LazyColumn(modifier = modifier) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Recent Transaction", fontSize = 20.sp)
                Text(
                    text = "See All",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
        items(list) {
            TransactionListItem(
                title = it.title,
                amount = it.amount.toString(),
                icon = viewModel.getIcon(it),
                date = it.date,
                color = if (it.type == "Income") Color.Green else Color.Red
            )
        }
    }
}

@Composable
fun TransactionListItem(title: String, amount: String, icon: Int, date: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row {
            Image(
                painter = painterResource(id = icon), contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = CircleShape)
                    .border(2.dp, Zinc, CircleShape)
                    .background(Color.Gray)
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

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(rememberNavController())
}
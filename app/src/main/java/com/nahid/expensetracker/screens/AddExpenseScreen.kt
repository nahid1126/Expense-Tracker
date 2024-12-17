@file:OptIn(ExperimentalMaterial3Api::class)

package com.nahid.expensetracker.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nahid.expensetracker.R
import com.nahid.expensetracker.model.data.Expense
import com.nahid.expensetracker.ui.theme.Zinc
import com.nahid.expensetracker.view_model.AddExpenseViewModel
import com.nahid.expensetracker.view_model.AddExpenseViewModelFactory
import kotlinx.coroutines.launch

private const val TAG = "AddExpanseScreen"

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddExpenseScreen(rememberNavController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: AddExpenseViewModel =
        AddExpenseViewModelFactory(context).create(AddExpenseViewModel::class.java)
    Surface(modifier = Modifier.fillMaxSize()) {
        coroutineScope.launch {
            viewModel.message.collect { message ->
                if (message.contains("expense added successfully", ignoreCase = true)) {
                    rememberNavController.popBackStack()
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, list, card, topBar) = createRefs()
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
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "Add Expense",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(
                            Alignment.Center
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_dot),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

            }

            DataForm(
                modifier = Modifier
                    .padding(top = 80.dp)
                    .constrainAs(card) {
                        top.linkTo(nameRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, viewModel
            )
        }
    }
}


@Composable
fun DataForm(modifier: Modifier, viewModel: AddExpenseViewModel) {
    val context = LocalContext.current
    val expanseName = remember {
        mutableStateOf("")
    }
    val amount = remember {
        mutableStateOf("")
    }
    val expanseTypes = listOf("Income", "Expense")
    val category = listOf("Salary", "Basa", "Kalamoni", "Transport", "Others")
    var selectedExpenseCategory by remember {
        mutableStateOf("")
    }
    var selectedExpenseType by remember {
        mutableStateOf("")
    }
    val expenseDate = remember {
        mutableStateOf("")
    }
    val showDatePicker = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(value = expanseName.value, onValueChange = {
            expanseName.value = it
        }, label = {
            Text(text = "Enter Expense Title")
        }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.size(8.dp))

        DropDownMenu(
            listOfItems = expanseTypes,
            "Select Expense Type",
            onItemSelected = { selectedExpenseType = it })

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(
            value = amount.value,
            onValueChange = { amount.value = it },
            label = {
                Text(text = "Enter Expense Amount")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.size(8.dp))
        DropDownMenu(
            listOfItems = category,
            "Select Expense Category",
            onItemSelected = { selectedExpenseCategory = it })

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(
            value = expenseDate.value,
            onValueChange = { expenseDate.value = it },
            label = {
                Text(text = "Select Date")
            }, modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker.value = true }, enabled = false
        )

        Spacer(modifier = Modifier.size(8.dp))

        Button(
            onClick = {
                val expense = Expense(
                    null,
                    expanseName.value,
                    amount.value.trim().toIntOrNull() ?: 0,
                    expenseDate.value,
                    selectedExpenseType,
                    selectedExpenseCategory
                )
                viewModel.addExpanse(expense)
                Log.d(
                    TAG,
                    "DataForm: ${expanseName.value}, ${amount.value}, ${selectedExpenseType}, ${selectedExpenseCategory}, ${expenseDate.value}"
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Zinc),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
        ) {
            Text(text = "Add Expense", fontSize = 14.sp, color = Color.White)
        }

    }
    if (showDatePicker.value) {
        ExpenseDatePicker(
            onDateSelected = { date ->
                expenseDate.value = formatDate(date)
                showDatePicker.value = false
            },
            onDismiss = { showDatePicker.value = false }
        )
    }
}


@Composable
fun ExpenseDatePicker(
    onDateSelected: (date: Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L
    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDateSelected(selectedDate) }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancel")
            }
        }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun DropDownMenu(
    listOfItems: List<String>,
    hintText: String,
    onItemSelected: (item: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            label = {
                Text(text = hintText)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .onGloballyPositioned { textFieldSize = it.size.toSize() }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOfItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedText = item
                        onItemSelected(selectedText)
                        expanded = false

                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddExpenseScreen() {
    AddExpenseScreen(rememberNavController())
}

fun formatDate(dateMillis: Long): String {
    val formatter = java.text.SimpleDateFormat("dd-MMM-yyyy", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(dateMillis))
}

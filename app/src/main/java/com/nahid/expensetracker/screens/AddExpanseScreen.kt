package com.nahid.expensetracker.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.nahid.expensetracker.R
import com.nahid.expensetracker.ui.theme.Zinc

@Composable
fun AddExpanseScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
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
                    text = "Add Expanse",
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

            DataForm(modifier = Modifier
                .padding(top = 80.dp)
                .constrainAs(card) {
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataForm(modifier: Modifier) {
    val context = LocalContext.current
    val expanseTypes = arrayOf("Income", "Expanse")
    val category = arrayOf("Fixed", "Transport", "Others")
    var expandedCat by remember { mutableStateOf(false) }
    var selectedTextCat by remember { mutableStateOf("") }
    var textFieldSizeCat by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
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
        ExposedDropdownMenuBox(
            expanded = expandedCat,
            onExpandedChange = { expandedCat = !expandedCat },
        ) {
            OutlinedTextField(
                value = selectedTextCat,
                onValueChange = {selectedTextCat = it},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                label = {
                    Text(text = "Select Category")
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .onGloballyPositioned { textFieldSizeCat = it.size.toSize() }
            )
            ExposedDropdownMenu(
                expanded = expandedCat,
                onDismissRequest = { expandedCat = false }
            ) {
                category.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedTextCat = item
                            expandedCat = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {selectedText = it},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                label = {
                    Text(text = "Select Expanse Type")
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
                expanseTypes.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            selectedText = item
                            expanded = false
                            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(value = "", onValueChange = {}, label = {
            Text(text = "Enter Expanse Name")
        }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(
            value =
            "",
            onValueChange = {},
            label = {
                Text(text = "Enter Expanse Amount")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedTextField(value = "", onValueChange = {}, label = {
            Text(text = "Enter Date")
        }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.size(8.dp))

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(containerColor = Zinc),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
        ) {
            Text(text = "Add Expanse", fontSize = 14.sp, color = Color.White)
        }

    }
}

@Composable
@Preview(showBackground = true)
fun AddExpansePreview() {
    AddExpanseScreen()
}
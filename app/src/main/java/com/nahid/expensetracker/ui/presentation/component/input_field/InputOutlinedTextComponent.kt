package com.nahid.expensetracker.ui.presentation.component.input_field

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.core.utils.extension.checkInputType
import com.nahid.expensetracker.core.utils.extension.equalIgnoreCase

private const val TAG = "InputOutlinedTextComponent"

@Composable
fun InputOutlinedTextComponent(question: InputQuestion, onAnswerChange: (String) -> Unit) {
    val modifier = if (question.inputFieldSize.equalIgnoreCase("LARGE")) {
        Modifier.fillMaxWidth()
    } else if (question.inputFieldSize.equalIgnoreCase("MEDIUM")) {
        Modifier.fillMaxWidth(0.7f)
    } else {
        Modifier.fillMaxWidth(0.5f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppConstants.APP_MARGIN.dp)
            .padding(horizontal = AppConstants.APP_MARGIN.dp)
    ) {

        val inputType = when (question.inputType) {
            "ONLY_NUMBER" -> KeyboardType.Number
            "DECIMAL_NUMBER" -> KeyboardType.Number
            "PASSWORD" -> KeyboardType.Password
            else -> KeyboardType.Text
        }


        OutlinedTextField(
            value = question.textInput,
            onValueChange = {
                if (question.textLimit >= it.length && it.checkInputType(question.questionText)
                ) {
                    onAnswerChange(it)
                }
            },
            label = {
                Text(question.textHint)
            },
            keyboardOptions = KeyboardOptions(keyboardType = inputType),
            visualTransformation = if (question.inputType.equalIgnoreCase("PASSWORD")) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            singleLine = question.minLines == 1,
            minLines = question.minLines,
            modifier = modifier,
        )
    }




}





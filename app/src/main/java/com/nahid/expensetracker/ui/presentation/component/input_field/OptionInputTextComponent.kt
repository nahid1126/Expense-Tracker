package com.nahid.expensetracker.ui.presentation.component.input_field

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nahid.expensetracker.core.AppConstants
import com.nahid.expensetracker.core.utils.extension.checkInputType
import com.nahid.expensetracker.core.utils.extension.equalIgnoreCase

@Composable
fun OptionInputTextComponent(question: InputQuestion, onAnswerChange: (String) -> Unit) {
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
    ) {

        val inputType = when (question.inputType) {
            "ONLY_NUMBER" -> KeyboardType.Number
            "DECIMAL_NUMBER" -> KeyboardType.Number
            "PASSWORD" -> KeyboardType.Password
            else -> KeyboardType.Text
        }

        var text by rememberSaveable { mutableStateOf(question.textInput.ifEmpty { "" }) }


        Box(
            modifier = Modifier
            //.padding(top = 2.dp)
            /* .border(
                 width = 1.dp,
                 color = MaterialTheme.colorScheme.onBackground,
                 shape = RoundedCornerShape(2.dp)
             )*/
            // .padding(all = 2.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = {
                    if (question.textLimit >= it.length && it.checkInputType(question.questionText)
                    ) {
                        text = it
                        onAnswerChange(text)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = inputType),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,

                modifier = modifier
                    .padding(0.dp),
                decorationBox = { innerTextField ->
                    Box(Modifier.padding((AppConstants.APP_MARGIN / 3).dp)) {
                        if (text.isEmpty()) {
                            Text(
                                text = question.textHint,
                                style = MaterialTheme.typography.bodyMedium.copy(color = Gray)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }


}

@Composable
fun textFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary
    )
}





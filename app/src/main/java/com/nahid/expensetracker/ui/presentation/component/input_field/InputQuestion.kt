package com.nahid.expensetracker.ui.presentation.component.input_field

import kotlinx.serialization.Serializable

@Serializable
data class InputQuestion(
    val questionText: String,
    val inputType: String,
    val inputFieldSize: String,
    val textLimit: Int = 200,
    val textHint: String,
    val textInput: String = "",
    val minLines: Int = 1
)

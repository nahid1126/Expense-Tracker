package com.nahid.expensetracker.domain.uiconfig

import androidx.compose.ui.graphics.Color

data class QuickAction(
    val title: String,
    val icon: Int,
    val color: Color,
    val onClick: () -> Unit
)
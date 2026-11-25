package com.nahid.expensetracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val defaults = Typography()

val Typography = Typography(
    displayLarge = defaults.displayLarge.copy(fontFamily = FontFamily.SansSerif),
    displayMedium = defaults.displayMedium.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-1.2).sp,
    ),
    headlineSmall = defaults.headlineSmall.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = defaults.titleLarge.copy(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    titleMedium = defaults.titleMedium.copy(fontFamily = FontFamily.SansSerif),
    titleSmall = defaults.titleSmall.copy(fontFamily = FontFamily.SansSerif),
    bodyLarge = defaults.bodyLarge.copy(fontFamily = FontFamily.SansSerif, fontSize = 15.sp),
    bodyMedium = defaults.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
    bodySmall = defaults.bodySmall.copy(fontFamily = FontFamily.SansSerif),
    labelLarge = defaults.labelLarge.copy(fontFamily = FontFamily.SansSerif),
    labelMedium = defaults.labelMedium.copy(fontFamily = FontFamily.SansSerif),
)

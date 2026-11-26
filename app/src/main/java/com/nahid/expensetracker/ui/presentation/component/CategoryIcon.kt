package com.nahid.expensetracker.ui.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Locale

internal fun categoryIcon(category: String): ImageVector = when (category.trim().lowercase(Locale.ROOT)) {
    "home", "home rent", "housing", "rent" -> Icons.Default.Home
    "food", "groceries", "dining", "restaurants" -> Icons.Default.Restaurant
    "transport", "transportation", "travel", "fuel" -> Icons.Default.DirectionsBus
    "shopping", "clothing", "clothing & shoes" -> Icons.Default.ShoppingBag
    "salary", "income", "freelance", "bonus" -> Icons.Default.Payments
    "deposit" -> Icons.Default.Savings
    "pocket money" -> Icons.Default.AccountBalanceWallet
    "health", "medical", "healthcare" -> Icons.Default.MedicalServices
    "education" -> Icons.Default.School
    "entertainment" -> Icons.Default.ConfirmationNumber
    "other", "others" -> Icons.Default.MoreHoriz
    else -> Icons.Default.Category
}

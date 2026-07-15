package com.nahid.expensetracker.domain.model

data class Expense(
    val id: Int? = null,
    val title: String,
    val amount: Int,
    val date: String,
    val type: String,
    val category: String,
    val isSynced: Boolean = false
)

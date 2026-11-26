package com.nahid.expensetracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpenseCategory(
    val id: Long = 0,
    val name: String = ""
)

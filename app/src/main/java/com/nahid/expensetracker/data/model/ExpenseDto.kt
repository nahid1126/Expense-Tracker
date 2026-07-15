package com.nahid.expensetracker.data.model

import androidx.annotation.Keep
import com.nahid.expensetracker.domain.model.Expense

@Keep
data class ExpenseDto(
    val id: Int? = null,
    val title: String = "",
    val amount: Int = 0,
    val date: String = "",
    val type: String = "",
    val category: String = ""
) {
    fun toDomain() = Expense(
        id = id,
        title = title,
        amount = amount,
        date = date,
        type = type,
        category = category,
        isSynced = true // If it's coming from Firebase, it's synced
    )
}

fun Expense.toDto() = ExpenseDto(
    id = id,
    title = title,
    amount = amount,
    date = date,
    type = type,
    category = category
)

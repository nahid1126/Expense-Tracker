package com.nahid.expensetracker.data.model

import androidx.annotation.Keep
import com.nahid.expensetracker.domain.model.ExpenseCategory

@Keep
data class ExpenseCategoryDto(
    val id: Long = 0,
    val name: String = ""
) {
    fun toDomain(): ExpenseCategory {
        return ExpenseCategory(
            id = id,
            name = name
        )
    }
}

fun ExpenseCategory.toDto(): ExpenseCategoryDto {
    return ExpenseCategoryDto(
        id = id,
        name = name
    )
}

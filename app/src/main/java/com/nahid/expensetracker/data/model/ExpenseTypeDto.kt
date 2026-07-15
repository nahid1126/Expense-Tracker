package com.nahid.expensetracker.data.model

import androidx.annotation.Keep
import com.nahid.expensetracker.domain.model.ExpenseType

@Keep
data class ExpenseTypeDto(
    val id: Long = 0,
    val name: String = ""
) {
    fun toDomain(): ExpenseType {
        return ExpenseType(
            id = id,
            name = name
        )
    }
}

fun ExpenseType.toDto(): ExpenseTypeDto {
    return ExpenseTypeDto(
        id = id,
        name = name
    )
}

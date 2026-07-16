package com.nahid.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nahid.expensetracker.domain.model.Expense
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String,
    val amount: Int,
    val date: String,
    val type: String,
    val category: String,
    val isSynced: Boolean = false,
)

fun ExpenseEntity.toDomain() = Expense(
    id = id,
    title = title,
    amount = amount,
    date = date,
    type = type,
    category = category,
    isSynced = isSynced
)

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    title = title,
    amount = amount,
    date = date,
    type = type,
    category = category,
    isSynced = isSynced
)
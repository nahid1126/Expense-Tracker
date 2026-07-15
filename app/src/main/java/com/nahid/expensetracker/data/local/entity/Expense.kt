package com.nahid.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String,
    val amount: Int,
    val date: String,
    val type: String,
    val category: String,
    val isSynced: Boolean = false,
)

fun Expense.toDomain() = Expense(
    id = id,
    title = title,
    amount = amount,
    date = date,
    type = type,
    category = category,
    isSynced = isSynced
)

fun Expense.toEntity() = Expense(
    id = id,
    title = title,
    amount = amount,
    date = date,
    type = type,
    category = category,
    isSynced = isSynced
)
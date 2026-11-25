package com.nahid.expensetracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nahid.expensetracker.domain.model.Expense

@Entity(indices = [Index(value = ["ownerUid", "remoteId"], unique = true)])
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val title: String,
    val amount: Int,
    val date: String,
    val type: String,
    val category: String,
    val isSynced: Boolean = false,
    @ColumnInfo(defaultValue = "''") val ownerUid: String,
    @ColumnInfo(defaultValue = "''") val remoteId: String,
    @ColumnInfo(defaultValue = "''") val revision: String = "",
    @ColumnInfo(defaultValue = "0") val isDeleted: Boolean = false,
)

fun ExpenseEntity.toDomain() = Expense(id, title, amount, date, type, category, isSynced)

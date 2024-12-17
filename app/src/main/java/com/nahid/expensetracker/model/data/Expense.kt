package com.nahid.expensetracker.model.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String,
    val amount: Int,
    val date: String,
    val type: String,
    val category: String
):Parcelable

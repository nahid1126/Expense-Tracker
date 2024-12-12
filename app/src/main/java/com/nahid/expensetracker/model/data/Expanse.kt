package com.nahid.expensetracker.model.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Expanse(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val name: String,
    val amount: Double,
    val date: Long,
    val type: String,
    val category: String
):Parcelable

package com.nahid.expensetracker.core.utils

import com.nahid.expensetracker.R
import com.nahid.expensetracker.data.local.entity.ExpenseEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {
    fun formatDate(dateMillis: Long): String {
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return formatter.format(Date(dateMillis))
    }

    fun formatDateFormat(dateMillis: Long): String {
        val formatter = SimpleDateFormat("dd-MMM", Locale.getDefault())
        return formatter.format(Date(dateMillis))
    }

    fun formatMilliFromDate(dateFormat: String): Long {
        var date = Date()
        val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        try {
            date = formatter.parse(dateFormat) as Date
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date.time
    }
    fun getIcon(expenseEntity: ExpenseEntity): Int {
        return when (expenseEntity.category) {
            "Salary" -> {
                R.drawable.ic_up
            }
            "Basa", "Kalamoni" -> {
                R.drawable.fixed
            }
            "Transport" -> {
                R.drawable.transport
            }
            else -> {
                R.drawable.others
            }
        }
    }


    fun getTimeOfDay(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> "Morning"
            12 -> "Noon"
            in 13..16 -> "Afternoon"
            in 17..19 -> "Evening"
            else -> "Night"
        }
    }

}
package com.nahid.expensetracker

import com.nahid.expensetracker.model.data.Expense
import java.util.Date
import java.util.Locale

object Utils {
    fun formatDate(dateMillis: Long): String {
        val formatter = java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return formatter.format(Date(dateMillis))
    }

    fun formatDateFormat(dateMillis: Long): String {
        val formatter = java.text.SimpleDateFormat("dd-MMM", Locale.getDefault())
        return formatter.format(Date(dateMillis))
    }

    fun formatMilliFromDate(dateFormat: String): Long {
        var date = Date()
        val formatter = java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        try {
            date = formatter.parse(dateFormat) as Date
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date.time
    }
    fun getIcon(expense: Expense): Int {
        return when (expense.category) {
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
}
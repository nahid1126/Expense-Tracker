package com.nahid.expensetracker

import java.util.Date
import java.util.Locale

object Utils {
    fun formatDate(dateMillis: Long): String {
        val formatter = java.text.SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return formatter.format(java.util.Date(dateMillis))
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
}
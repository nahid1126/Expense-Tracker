package com.nahid.expensetracker.core

import android.util.Log
import com.nahid.expensetracker.core.AppConstants

private const val TAG = "Logger"

object Logger {
    fun log(tag: String = TAG, message: String?){
        if (AppConstants.ENABLE_LOG){
            Log.d(tag, "$message")
        }
    }
}
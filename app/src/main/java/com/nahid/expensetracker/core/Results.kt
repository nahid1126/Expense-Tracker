package com.nahid.expensetracker.core

import com.nahid.expensetracker.core.utils.exception.AppException

sealed class Results<out T> {
    data class Success<T>(val data: T) : Results<T>()
    data class Error(val exception: AppException) : Results<Nothing>()
}
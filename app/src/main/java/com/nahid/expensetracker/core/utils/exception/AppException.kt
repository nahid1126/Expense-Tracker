package com.nahid.expensetracker.core.utils.exception


fun AppException.getMessage() = when (this) {
    is AppException.AuthException -> message
    is AppException.NetworkException -> message
    is AppException.ServerException -> message
    is AppException.OthersException -> message
    is AppException.ExitTimeException -> message
}
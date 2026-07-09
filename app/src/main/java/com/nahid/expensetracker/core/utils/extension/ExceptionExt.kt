package com.nahid.expensetracker.core.utils.extension

import android.util.Log
import com.nahid.expensetracker.core.utils.exception.AppException
import java.net.ConnectException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

private const val TAG = "ExceptionExt"

fun Exception.getErrorMessage():String{
    return this.message?:"Something Went Wrong"
}

fun Exception.getSpecificException(): AppException {
    Log.d(TAG, "getSpecificException: ${this.message}")
   return when(this){
        is UnresolvedAddressException -> AppException.NetworkException(message = "Please, Check Your Internet Connection")
        is UnknownHostException, is ConnectException -> AppException.NetworkException(message = "Unable to Communicate With Server")
        else -> AppException.OthersException(message = "Something Went Wrong")
    }
}
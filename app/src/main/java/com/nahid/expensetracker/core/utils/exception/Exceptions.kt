package com.nahid.expensetracker.core.utils.exception

sealed class AppException(message: String){
    data class AuthException(val message:String = "Authentication Failed") : AppException(message)
    data class ExitTimeException(val message:String = "") : AppException(message)
    data class NetworkException(val message:String = "Please, Check Your Internet Connection") : AppException(message)
    class ServerException(val message:String):AppException(message)
    class OthersException(val message: String) : AppException(message)
}



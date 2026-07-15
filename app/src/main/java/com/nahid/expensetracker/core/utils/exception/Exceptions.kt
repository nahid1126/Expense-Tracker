package com.nahid.expensetracker.core.utils.exception

sealed class AppException(open val message: String){
    data class AuthException(override val message:String = "Authentication Failed") : AppException(message)
    data class ExitTimeException(override val message:String = "") : AppException(message)
    data class NetworkException(override val message:String = "Please, Check Your Internet Connection") : AppException(message)
    class ServerException(override val message:String):AppException(message)
    class OthersException(override val message: String) : AppException(message)
}

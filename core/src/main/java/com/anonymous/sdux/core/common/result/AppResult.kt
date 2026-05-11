package com.anonymous.sdux.core.common.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val exception: AppException) : AppResult<Nothing>()
    object Loading : AppResult<Nothing>()
}

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class DatabaseException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class ValidationException(message: String) : AppException(message)
    class UnknownException(message: String, cause: Throwable? = null) : AppException(message, cause)
}

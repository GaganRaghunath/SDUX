package com.anonymous.domain.common.exception

sealed class DomainException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NetworkException(
        message: String = "Network error occurred",
        cause: Throwable? = null
    ) : DomainException(message, cause)

    class UnauthorizedException(
        message: String = "Unauthorized access"
    ) : DomainException(message)

    class NotFoundException(
        message: String = "Resource not found"
    ) : DomainException(message)

    class ValidationException(
        val errors: List<String>
    ) : DomainException("Validation failed: $errors")

    class ServerException(
        val code: Int,
        message: String = "Server error"
    ) : DomainException(message)

    class UnknownException(
        message: String = "An unknown error occurred",
        cause: Throwable? = null
    ) : DomainException(message, cause)
}

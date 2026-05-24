package com.anonymous.domain.common.result

import com.anonymous.domain.common.exception.DomainException

sealed class DomainResult<out T> {

    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Error(val exception: DomainException) : DomainResult<Nothing>()
    data object Loading : DomainResult<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading
}

inline fun <T> DomainResult<T>.onSuccess(action: (T) -> Unit): DomainResult<T> {
    if (this is DomainResult.Success) action(data)
    return this
}

inline fun <T> DomainResult<T>.onError(action: (DomainException) -> Unit): DomainResult<T> {
    if (this is DomainResult.Error) action(exception)
    return this
}

inline fun <T> DomainResult<T>.onLoading(action: () -> Unit): DomainResult<T> {
    if (this is DomainResult.Loading) action()
    return this
}

inline fun <T, R> DomainResult<T>.map(transform: (T) -> R): DomainResult<R> = when (this) {
    is DomainResult.Success -> DomainResult.Success(transform(data))
    is DomainResult.Error   -> this
    is DomainResult.Loading -> DomainResult.Loading
}

inline fun <T> DomainResult<T>.getOrElse(default: (DomainException) -> T): T = when (this) {
    is DomainResult.Success -> data
    is DomainResult.Error   -> default(exception)
    is DomainResult.Loading -> throw IllegalStateException("Result is still loading")
}

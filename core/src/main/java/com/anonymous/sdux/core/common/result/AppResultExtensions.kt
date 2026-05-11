package com.anonymous.sdux.core.common.result

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
    is AppResult.Loading -> AppResult.Loading
}

inline fun <T, R> AppResult<T>.flatMap(transform: (T) -> AppResult<R>): AppResult<R> = when (this) {
    is AppResult.Success -> transform(data)
    is AppResult.Error -> this
    is AppResult.Loading -> AppResult.Loading
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (AppException) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(exception)
    return this
}

inline fun <T> AppResult<T>.onLoading(action: () -> Unit): AppResult<T> {
    if (this is AppResult.Loading) action()
    return this
}

inline fun <T, R> AppResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (AppException) -> R,
    onLoading: () -> R,
): R = when (this) {
    is AppResult.Success -> onSuccess(data)
    is AppResult.Error -> onError(exception)
    is AppResult.Loading -> onLoading()
}

package com.anonymous.sdux.core.common.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import kotlinx.coroutines.withContext

abstract class UseCase<in P, R>(private val dispatcherProvider: DispatcherProvider) {
    suspend operator fun invoke(params: P): AppResult<R> = withContext(dispatcherProvider.io) {
        execute(params)
    }

    protected abstract suspend fun execute(params: P): AppResult<R>
}

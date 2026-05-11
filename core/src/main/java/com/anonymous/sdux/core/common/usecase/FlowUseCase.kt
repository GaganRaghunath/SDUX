package com.anonymous.sdux.core.common.usecase

import com.anonymous.sdux.core.common.dispatcher.DispatcherProvider
import com.anonymous.sdux.core.common.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, T>(private val dispatcherProvider: DispatcherProvider) {
    operator fun invoke(params: P): Flow<AppResult<T>> = execute(params).flowOn(dispatcherProvider.io)

    protected abstract fun execute(params: P): Flow<AppResult<T>>
}

package com.anonymous.domain.base.usecase

import com.anonymous.domain.common.result.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in Params, out Result>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke(params: Params): Flow<DomainResult<Result>> =
        execute(params).flowOn(dispatcher)

    protected abstract fun execute(params: Params): Flow<DomainResult<Result>>
}

package com.anonymous.domain.base.usecase

import com.anonymous.domain.common.result.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class UseCase<in Params, out Result>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(params: Params): DomainResult<Result> =
        withContext(dispatcher) { execute(params) }

    protected abstract suspend fun execute(params: Params): DomainResult<Result>
}

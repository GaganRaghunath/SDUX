package com.anonymous.domain.base.usecase

import com.anonymous.domain.common.result.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class NoParamUseCase<out Result>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): DomainResult<Result> =
        withContext(dispatcher) { execute() }

    protected abstract suspend fun execute(): DomainResult<Result>
}

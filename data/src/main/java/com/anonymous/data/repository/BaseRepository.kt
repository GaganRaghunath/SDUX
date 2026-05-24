package com.anonymous.data.repository

import com.anonymous.data.remote.api.safeApiCall
import com.anonymous.domain.common.result.DomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseRepository {

    protected suspend fun <T> apiCall(
        call: suspend () -> T
    ): DomainResult<T> = safeApiCall(call)

    protected fun <T> flowApiCall(
        call: suspend () -> T
    ): Flow<DomainResult<T>> = flow {
        emit(DomainResult.Loading)
        emit(safeApiCall(call))
    }
}

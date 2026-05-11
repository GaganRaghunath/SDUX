package com.anonymous.sdux.data.common.resource

import com.anonymous.sdux.core.common.result.AppException
import com.anonymous.sdux.core.common.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class NetworkBoundResource<LocalType, RemoteType, DomainType>(
    private val query: () -> Flow<LocalType>,
    private val fetch: suspend () -> RemoteType,
    private val saveFetchResult: suspend (RemoteType) -> Unit,
    private val mapToResult: (LocalType) -> DomainType,
    private val shouldFetch: (LocalType?) -> Boolean = { true },
) {
    fun asFlow(): Flow<AppResult<DomainType>> = flow {
        emit(AppResult.Loading)
        val localData = try {
            var latest: LocalType? = null
            query().collect { latest = it }
            latest
        } catch (e: Exception) {
            null
        }

        if (shouldFetch(localData)) {
            try {
                val remote = fetch()
                saveFetchResult(remote)
            } catch (e: Exception) {
                emit(AppResult.Error(AppException.NetworkException(e.message ?: "Fetch failed", e)))
                return@flow
            }
        }

        query().map { AppResult.Success(mapToResult(it)) }.collect { emit(it) }
    }
}

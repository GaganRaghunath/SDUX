package com.anonymous.data.remote.api

import com.anonymous.domain.common.exception.DomainException
import com.anonymous.domain.common.result.DomainResult
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(call: suspend () -> T): DomainResult<T> = try {
    DomainResult.Success(call())
} catch (e: HttpException) {
    val exception = when (e.code()) {
        401          -> DomainException.UnauthorizedException()
        404          -> DomainException.NotFoundException()
        in 500..599  -> DomainException.ServerException(e.code(), e.message())
        else         -> DomainException.NetworkException(e.message(), e)
    }
    DomainResult.Error(exception)
} catch (e: IOException) {
    DomainResult.Error(DomainException.NetworkException(cause = e))
} catch (e: Exception) {
    DomainResult.Error(DomainException.UnknownException(cause = e))
}

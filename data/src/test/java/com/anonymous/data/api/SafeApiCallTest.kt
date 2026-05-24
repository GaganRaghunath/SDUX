package com.anonymous.data.api

import com.anonymous.data.remote.api.safeApiCall
import com.anonymous.domain.common.exception.DomainException
import com.anonymous.domain.common.result.DomainResult
import com.anonymous.testshared.base.BaseUnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SafeApiCallTest : BaseUnitTest() {

    // ── Happy path ────────────────────────────────────────────────────────────

    @Test
    fun `successful call - returns Success with data`() = runTest {
        val result = safeApiCall { "payload" }
        result shouldBe DomainResult.Success("payload")
    }

    @Test
    fun `successful call - works with null value`() = runTest {
        val result = safeApiCall<String?> { null }
        result shouldBe DomainResult.Success(null)
    }

    // ── IOException ───────────────────────────────────────────────────────────

    @Test
    fun `IOException - maps to NetworkException`() = runTest {
        val result = safeApiCall<String> { throw IOException("timeout") }
        val error = result as DomainResult.Error
        error.exception shouldBeInstanceOf DomainException.NetworkException::class
    }

    @Test
    fun `IOException - preserves cause`() = runTest {
        val cause = IOException("connection reset")
        val result = safeApiCall<String> { throw cause }
        val error = result as DomainResult.Error
        error.exception.cause shouldBe cause
    }

    // ── HttpException ─────────────────────────────────────────────────────────

    @Test
    fun `401 - maps to UnauthorizedException`() = runTest {
        val result = safeApiCall<String> { throw httpError(401) }
        (result as DomainResult.Error).exception shouldBeInstanceOf DomainException.UnauthorizedException::class
    }

    @Test
    fun `404 - maps to NotFoundException`() = runTest {
        val result = safeApiCall<String> { throw httpError(404) }
        (result as DomainResult.Error).exception shouldBeInstanceOf DomainException.NotFoundException::class
    }

    @Test
    fun `500 - maps to ServerException with correct code`() = runTest {
        val result = safeApiCall<String> { throw httpError(500) }
        val ex = (result as DomainResult.Error).exception as DomainException.ServerException
        ex.code shouldBe 500
    }

    @Test
    fun `503 - maps to ServerException with correct code`() = runTest {
        val result = safeApiCall<String> { throw httpError(503) }
        val ex = (result as DomainResult.Error).exception as DomainException.ServerException
        ex.code shouldBe 503
    }

    @Test
    fun `other HTTP codes - map to NetworkException`() = runTest {
        val result = safeApiCall<String> { throw httpError(429) }
        (result as DomainResult.Error).exception shouldBeInstanceOf DomainException.NetworkException::class
    }

    // ── Generic exception ─────────────────────────────────────────────────────

    @Test
    fun `unexpected exception - maps to UnknownException`() = runTest {
        val result = safeApiCall<String> { throw RuntimeException("boom") }
        (result as DomainResult.Error).exception shouldBeInstanceOf DomainException.UnknownException::class
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun httpError(code: Int): HttpException =
        HttpException(Response.error<String>(code, "".toResponseBody()))
}

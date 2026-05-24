package com.anonymous.domain.result

import com.anonymous.domain.common.exception.DomainException
import com.anonymous.domain.common.result.DomainResult
import com.anonymous.domain.common.result.getOrElse
import com.anonymous.domain.common.result.map
import com.anonymous.domain.common.result.onError
import com.anonymous.domain.common.result.onSuccess
import com.anonymous.testshared.base.BaseUnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Test

class DomainResultTest : BaseUnitTest() {

    // ── isSuccess / isError / isLoading flags ─────────────────────────────────

    @Test
    fun `Success - isSuccess is true`() {
        DomainResult.Success("data").isSuccess shouldBe true
    }

    @Test
    fun `Error - isError is true`() {
        DomainResult.Error(DomainException.NetworkException()).isError shouldBe true
    }

    @Test
    fun `Loading - isLoading is true`() {
        DomainResult.Loading.isLoading shouldBe true
    }

    // ── onSuccess ─────────────────────────────────────────────────────────────

    @Test
    fun `onSuccess - fires callback with data`() {
        var captured: String? = null
        DomainResult.Success("hello").onSuccess { captured = it }
        captured shouldBe "hello"
    }

    @Test
    fun `onSuccess - skipped for Error`() {
        var called = false
        DomainResult.Error(DomainException.NetworkException()).onSuccess { called = true }
        called shouldBe false
    }

    // ── onError ───────────────────────────────────────────────────────────────

    @Test
    fun `onError - fires callback with exception`() {
        val ex = DomainException.NotFoundException()
        var captured: DomainException? = null
        DomainResult.Error(ex).onError { captured = it }
        captured shouldBeInstanceOf DomainException.NotFoundException::class
    }

    @Test
    fun `onError - skipped for Success`() {
        var called = false
        DomainResult.Success(1).onError { called = true }
        called shouldBe false
    }

    // ── map ───────────────────────────────────────────────────────────────────

    @Test
    fun `map - transforms Success value`() {
        val result = DomainResult.Success(4).map { it * 2 }
        result shouldBe DomainResult.Success(8)
    }

    @Test
    fun `map - passes Error through unchanged`() {
        val error = DomainException.UnauthorizedException()
        val result = DomainResult.Error(error).map<Nothing, Int> { 42 }
        result shouldBe DomainResult.Error(error)
    }

    @Test
    fun `map - passes Loading through unchanged`() {
        val result = DomainResult.Loading.map<Nothing, Int> { 42 }
        result shouldBe DomainResult.Loading
    }

    // ── getOrElse ─────────────────────────────────────────────────────────────

    @Test
    fun `getOrElse - returns data for Success`() {
        DomainResult.Success("value").getOrElse { "default" } shouldBe "value"
    }

    @Test
    fun `getOrElse - invokes fallback for Error`() {
        val result = DomainResult.Error(DomainException.NetworkException())
            .getOrElse { "fallback" }
        result shouldBe "fallback"
    }

    @Test
    fun `getOrElse - throws for Loading`() {
        var threw = false
        try {
            DomainResult.Loading.getOrElse { "x" }
        } catch (e: IllegalStateException) {
            threw = true
        }
        threw shouldBe true
    }

    // ── DomainException subtypes ──────────────────────────────────────────────

    @Test
    fun `ValidationException - carries error list`() {
        val ex = DomainException.ValidationException(listOf("required", "too short"))
        ex.errors shouldBe listOf("required", "too short")
    }

    @Test
    fun `ServerException - carries HTTP code`() {
        val ex = DomainException.ServerException(code = 503)
        ex.code shouldBe 503
    }
}

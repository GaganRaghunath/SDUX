package com.anonymous.domain.usecase

import com.anonymous.domain.base.usecase.UseCase
import com.anonymous.domain.common.exception.DomainException
import com.anonymous.domain.common.result.DomainResult
import com.anonymous.testshared.base.BaseUnitTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import org.junit.Test

class UseCaseTest : BaseUnitTest() {

    // ── Concrete stub use case ─────────────────────────────────────────────────

    private val lengthUseCase = object : UseCase<String, Int>(coroutineRule.dispatcher) {
        override suspend fun execute(params: String) = DomainResult.Success(params.length)
    }

    private val failingUseCase = object : UseCase<Unit, String>(coroutineRule.dispatcher) {
        override suspend fun execute(params: Unit) =
            DomainResult.Error(DomainException.NetworkException("offline"))
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    fun `invoke - returns Success with mapped value`() = runTest {
        val result = lengthUseCase("hello")
        result shouldBe DomainResult.Success(5)
    }

    @Test
    fun `invoke - empty string returns Success with zero length`() = runTest {
        val result = lengthUseCase("")
        result shouldBe DomainResult.Success(0)
    }

    @Test
    fun `invoke - returns Error from execute`() = runTest {
        val result = failingUseCase(Unit)
        result.isError shouldBe true
        (result as DomainResult.Error).exception shouldBeInstanceOf DomainException.NetworkException::class
    }

    @Test
    fun `execute - called exactly once per invoke`() = runTest {
        val spy = spyk(lengthUseCase)
        spy("test")
        coVerify(exactly = 1) { spy.execute("test") }
    }
}

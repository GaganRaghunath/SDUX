package com.anonymous.domain.usecase

import com.anonymous.domain.base.usecase.FlowUseCase
import com.anonymous.domain.common.exception.DomainException
import com.anonymous.domain.common.result.DomainResult
import com.anonymous.testshared.base.BaseUnitTest
import com.anonymous.testshared.extensions.testCollect
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.flow
import org.junit.Test

class FlowUseCaseTest : BaseUnitTest() {

    private val successUseCase = object : FlowUseCase<String, Int>(coroutineRule.dispatcher) {
        override fun execute(params: String) = flow {
            emit(DomainResult.Loading)
            emit(DomainResult.Success(params.length))
        }
    }

    private val errorUseCase = object : FlowUseCase<Unit, String>(coroutineRule.dispatcher) {
        override fun execute(params: Unit) = flow<DomainResult<String>> {
            emit(DomainResult.Loading)
            emit(DomainResult.Error(DomainException.ServerException(500)))
        }
    }

    @Test
    fun `invoke - emits Loading then Success`() = runTest {
        successUseCase("hello").testCollect {
            awaitItem() shouldBe DomainResult.Loading
            awaitItem() shouldBe DomainResult.Success(5)
            awaitComplete()
        }
    }

    @Test
    fun `invoke - emits Loading then Error`() = runTest {
        errorUseCase(Unit).testCollect {
            awaitItem() shouldBe DomainResult.Loading
            val error = awaitItem() as DomainResult.Error
            error.exception shouldBeInstanceOf DomainException.ServerException::class
            awaitComplete()
        }
    }

    @Test
    fun `invoke - can be collected multiple times independently`() = runTest {
        var first: DomainResult<Int>? = null
        var second: DomainResult<Int>? = null

        successUseCase("hi").testCollect {
            awaitItem()           // Loading
            first = awaitItem()   // Success
            awaitComplete()
        }
        successUseCase("hello").testCollect {
            awaitItem()           // Loading
            second = awaitItem()  // Success
            awaitComplete()
        }

        first shouldBe DomainResult.Success(2)
        second shouldBe DomainResult.Success(5)
    }
}

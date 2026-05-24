package com.anonymous.data.repository

import com.anonymous.domain.common.exception.DomainException
import com.anonymous.domain.common.result.DomainResult
import com.anonymous.testshared.base.BaseUnitTest
import com.anonymous.testshared.extensions.testCollect
import com.anonymous.testshared.factory.MockFactory
import com.anonymous.testshared.factory.StubFactory
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import org.junit.Test
import java.io.IOException

class BaseRepositoryTest : BaseUnitTest() {

    // Minimal subclass that exposes flowApiCall and apiCall for testing
    private inner class TestRepository : BaseRepository() {
        fun fetchFlow(succeed: Boolean, value: String = "result") =
            flowApiCall { if (succeed) value else throw IOException("offline") }

        suspend fun fetchSingle(succeed: Boolean, value: String = "result") =
            apiCall { if (succeed) value else throw IOException("offline") }
    }

    private val repo = TestRepository()

    // ── flowApiCall ───────────────────────────────────────────────────────────

    @Test
    fun `flowApiCall - emits Loading then Success`() = runTest {
        repo.fetchFlow(succeed = true, value = "data").testCollect {
            awaitItem() shouldBe DomainResult.Loading
            awaitItem() shouldBe DomainResult.Success("data")
            awaitComplete()
        }
    }

    @Test
    fun `flowApiCall - emits Loading then Error on IOException`() = runTest {
        repo.fetchFlow(succeed = false).testCollect {
            awaitItem() shouldBe DomainResult.Loading
            val error = awaitItem() as DomainResult.Error
            error.exception shouldBeInstanceOf DomainException.NetworkException::class
            awaitComplete()
        }
    }

    @Test
    fun `flowApiCall - emits exactly two items`() = runTest {
        val items = mutableListOf<DomainResult<String>>()
        repo.fetchFlow(succeed = true).testCollect {
            items += awaitItem()
            items += awaitItem()
            awaitComplete()
        }
        items.size shouldBe 2
    }

    // ── apiCall ───────────────────────────────────────────────────────────────

    @Test
    fun `apiCall - returns Success directly`() = runTest {
        val result = repo.fetchSingle(succeed = true, value = "ok")
        result shouldBe DomainResult.Success("ok")
    }

    @Test
    fun `apiCall - returns Error on failure`() = runTest {
        val result = repo.fetchSingle(succeed = false)
        result.isError shouldBe true
    }

    // ── MockFactory usage ─────────────────────────────────────────────────────

    @Test
    fun `StubFactory - generates unique string values`() {
        val a = StubFactory.anyString("repo")
        val b = StubFactory.anyString("repo")
        (a == b) shouldBe false
    }
}

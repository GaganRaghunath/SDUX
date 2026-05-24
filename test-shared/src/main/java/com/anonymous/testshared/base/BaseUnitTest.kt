package com.anonymous.testshared.base

import com.anonymous.testshared.rules.MainCoroutineRule
import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseUnitTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    open fun setUp() = Unit

    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }

    // Shares the same scheduler as coroutineRule so withContext / viewModelScope
    // coroutines are driven by the same advanceUntilIdle() call.
    fun runTest(block: suspend TestScope.() -> Unit) =
        runTest(coroutineRule.dispatcher, testBody = block)
}

package com.anonymous.testshared.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

/**
 * Collapses all dispatcher slots onto a single [TestDispatcher] so
 * runTest / advanceUntilIdle drives the full execution graph uniformly.
 */
class TestDispatcherProvider(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) {
    val main: CoroutineDispatcher get() = testDispatcher
    val io: CoroutineDispatcher get() = testDispatcher
    val default: CoroutineDispatcher get() = testDispatcher
    val unconfined: CoroutineDispatcher get() = testDispatcher
}

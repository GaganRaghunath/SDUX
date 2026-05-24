package com.anonymous.testshared.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

fun runUnitTest(
    dispatcher: TestDispatcher? = null,
    block: suspend TestScope.() -> Unit
) = if (dispatcher != null) {
    kotlinx.coroutines.test.runTest(dispatcher, testBody = block)
} else {
    runTest(testBody = block)
}

suspend fun TestScope.advanceAndIdle() {
    testScheduler.advanceUntilIdle()
}

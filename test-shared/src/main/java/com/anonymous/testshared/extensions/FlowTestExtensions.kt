package com.anonymous.testshared.extensions

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList

suspend fun <T> Flow<T>.testCollect(block: suspend TurbineTestContext<T>.() -> Unit) =
    test(validate = block)

suspend fun <T> StateFlow<T>.testStates(block: suspend TurbineTestContext<T>.() -> Unit) =
    test(validate = block)

suspend fun <T> Flow<T>.collectValues(count: Int): List<T> =
    toList().take(count)

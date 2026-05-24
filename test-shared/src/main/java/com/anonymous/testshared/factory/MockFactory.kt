package com.anonymous.testshared.factory

import io.mockk.mockk
import io.mockk.spyk

object MockFactory {

    inline fun <reified T : Any> mock(
        relaxed: Boolean = false,
        relaxUnitFun: Boolean = false,
        block: T.() -> Unit = {}
    ): T = mockk(relaxed = relaxed, relaxUnitFun = relaxUnitFun, block = block)

    inline fun <reified T : Any> relaxed(): T = mockk(relaxed = true)

    inline fun <reified T : Any> relaxedUnit(): T = mockk(relaxUnitFun = true)

    inline fun <reified T : Any> spy(obj: T, block: T.() -> Unit = {}): T =
        spyk(obj, block = block)
}

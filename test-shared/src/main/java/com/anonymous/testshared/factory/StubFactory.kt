package com.anonymous.testshared.factory

import java.util.UUID
import kotlin.random.Random

object StubFactory {

    fun anyString(prefix: String = "stub"): String = "$prefix-${UUID.randomUUID()}"

    fun anyInt(range: IntRange = 1..1_000): Int = range.random()

    fun anyLong(range: LongRange = 1L..1_000L): Long = range.random()

    fun anyBoolean(): Boolean = Random.nextBoolean()

    fun anyDouble(from: Double = 0.0, until: Double = 1_000.0): Double =
        Random.nextDouble(from, until)

    fun <T> listOf(size: Int = 3, factory: (index: Int) -> T): List<T> =
        kotlin.collections.List(size, factory)

    fun <T> single(factory: () -> T): T = factory()
}

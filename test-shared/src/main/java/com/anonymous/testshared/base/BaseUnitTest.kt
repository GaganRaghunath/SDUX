package com.anonymous.testshared.base

import com.anonymous.testshared.rules.MainCoroutineRule
import io.mockk.clearAllMocks
import io.mockk.unmockkAll
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
}

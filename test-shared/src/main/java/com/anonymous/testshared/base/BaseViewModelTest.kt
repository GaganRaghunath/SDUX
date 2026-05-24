package com.anonymous.testshared.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

/**
 * Extends [BaseUnitTest] with [InstantTaskExecutorRule] so Architecture Components
 * (LiveData, Transformations) execute synchronously on the test thread.
 *
 * Use this base for ViewModel tests. [coroutineRule] from [BaseUnitTest] already
 * replaces Dispatchers.Main with a [kotlinx.coroutines.test.TestDispatcher].
 */
abstract class BaseViewModelTest : BaseUnitTest() {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()
}

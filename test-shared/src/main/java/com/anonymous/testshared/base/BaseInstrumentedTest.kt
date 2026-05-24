package com.anonymous.testshared.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider

abstract class BaseInstrumentedTest {

    val appContext: Context
        get() = ApplicationProvider.getApplicationContext()
}

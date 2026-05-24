package com.anonymous.sdux.base.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.anonymous.sdux.ui.theme.SDUXTheme

abstract class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SDUXTheme {
                Content()
            }
        }
    }

    @Composable
    abstract fun Content()
}

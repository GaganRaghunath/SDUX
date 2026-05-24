package com.anonymous.sdux

import androidx.compose.runtime.Composable
import com.anonymous.sdux.base.ui.BaseActivity
import com.anonymous.sdux.framework.navigation.AppNavHost
import com.anonymous.sdux.framework.navigation.AppRoute
import com.anonymous.sdux.framework.navigation.NavGraphRegistry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var navGraphRegistry: NavGraphRegistry

    @Composable
    override fun Content() {
        AppNavHost(
            startRoute = AppRoute.Home,
            graphs     = navGraphRegistry.graphs
        )
    }
}

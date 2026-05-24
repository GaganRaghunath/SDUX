package com.anonymous.sdux.framework.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.anonymous.sdux.base.navigation.AppNavigator
import com.anonymous.sdux.base.navigation.FeatureNavGraph
import com.anonymous.sdux.base.navigation.LocalAppNavigator
import com.anonymous.sdux.base.navigation.rememberAppNavigator

/**
 * Root navigation host for the entire app.
 *
 * Wires Navigation 3's [NavDisplay] to the Hilt-provided [FeatureNavGraph] set.
 * Every route dispatches to whichever graph claims it via [FeatureNavGraph.handles].
 *
 * Usage in MainActivity:
 *
 *   AppNavHost(
 *       startRoute = AppRoute.Home,
 *       graphs     = navGraphRegistry.graphs
 *   )
 */
@Composable
fun AppNavHost(
    startRoute: AppRoute = AppRoute.Home,
    graphs: Set<FeatureNavGraph>
) {
    val backStack: NavBackStack<NavKey> = rememberNavBackStack(startRoute)

    val navigator: AppNavigator = rememberAppNavigator(backStack)

    CompositionLocalProvider(LocalAppNavigator provides navigator) {
        NavDisplay(
            backStack     = backStack,
            onBack        = navigator::navigateBack,
            entryProvider = { route ->
                NavEntry(key = route) {
                    graphs
                        .firstOrNull { it.handles(route) }
                        ?.Destination(route, navigator)
                        ?: error("No FeatureNavGraph registered for route: ${route::class.simpleName}")
                }
            }
        )
    }
}

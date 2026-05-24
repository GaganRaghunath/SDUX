package com.anonymous.sdux.framework.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    // Navigation 3 back stack — typed as SnapshotStateList<Any> so every
    // AppRoute subtype can be pushed without unchecked-cast warnings at call sites.
    @Suppress("UNCHECKED_CAST")
    val backStack: SnapshotStateList<Any> =
        rememberNavBackStack(startRoute) as SnapshotStateList<Any>

    val navigator: AppNavigator = rememberAppNavigator(backStack)

    CompositionLocalProvider(LocalAppNavigator provides navigator) {
        NavDisplay(
            backStack = backStack,
            onBack    = navigator::navigateBack
        ) { entry ->
            graphs
                .firstOrNull { it.handles(entry) }
                ?.Destination(entry, navigator)
                ?: error("No FeatureNavGraph registered for route: ${entry::class.simpleName}")
        }
    }
}

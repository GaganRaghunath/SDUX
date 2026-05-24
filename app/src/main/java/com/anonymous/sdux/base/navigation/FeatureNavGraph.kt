package com.anonymous.sdux.base.navigation

import androidx.compose.runtime.Composable

/**
 * Contract every feature nav graph must satisfy.
 *
 * Usage:
 *  1. Implement [handles] to claim ownership of a route type.
 *  2. Implement [Destination] to render the screen for that route.
 *  3. Bind the implementation via Hilt @IntoSet multibinding in the
 *     feature's own NavigationModule so [NavGraphRegistry] picks it up.
 */
interface FeatureNavGraph {

    /** Returns true if this graph owns [route]. */
    fun handles(route: Any): Boolean

    /** Renders the composable destination for [route]. */
    @Composable
    fun Destination(route: Any, navigator: AppNavigator)
}

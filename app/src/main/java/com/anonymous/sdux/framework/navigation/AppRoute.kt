package com.anonymous.sdux.framework.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Marker interface for all app-level routes.
 *
 * Each feature defines its own sealed interface that extends [AppRoute]:
 *
 *   sealed interface AuthRoute : AppRoute {
 *       data object Login    : AuthRoute
 *       data object Register : AuthRoute
 *   }
 *
 * This keeps every feature's routes self-contained while sharing a common type
 * for the back stack. Route dispatch is handled by [FeatureNavGraph.handles],
 * so [AppRoute] does not need to be sealed.
 *
 * Extends [NavKey] so all routes can be placed in a [NavBackStack].
 */
interface AppRoute : NavKey {

    /** Default landing destination when the app starts cold. */
    @Serializable
    data object Home : AppRoute
}

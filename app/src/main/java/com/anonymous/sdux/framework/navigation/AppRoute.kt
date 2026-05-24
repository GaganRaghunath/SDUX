package com.anonymous.sdux.framework.navigation

/**
 * Root sealed interface for all app-level routes.
 *
 * Each feature defines its own nested sealed interface that extends [AppRoute]:
 *
 *   sealed interface AuthRoute : AppRoute {
 *       data object Login    : AuthRoute
 *       data object Register : AuthRoute
 *   }
 *
 * This keeps every feature's routes self-contained while sharing a common type
 * for the back stack.
 */
sealed interface AppRoute {

    /** Default landing destination when the app starts cold. */
    data object Home : AppRoute
}

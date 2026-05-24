package com.anonymous.sdux.feature.auth.navigation

import com.anonymous.sdux.framework.navigation.AppRoute
import kotlinx.serialization.Serializable

/**
 * All navigation destinations owned by the Auth feature.
 * Add a new entry here whenever Auth needs a new screen.
 */
sealed interface AuthRoute : AppRoute {

    @Serializable
    data object Login : AuthRoute

    @Serializable
    data object Register : AuthRoute

    /** Landed here after a successful login; clears auth from the back stack. */
    @Serializable
    data object ForgotPassword : AuthRoute
}
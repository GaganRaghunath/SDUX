package com.anonymous.sdux.feature.auth.navigation

import com.anonymous.sdux.framework.navigation.AppRoute

/**
 * All navigation destinations owned by the Auth feature.
 * Add a new entry here whenever Auth needs a new screen.
 */
sealed interface AuthRoute : AppRoute {

    data object Login : AuthRoute

    data object Register : AuthRoute

    /** Landed here after a successful login; clears auth from the back stack. */
    data object ForgotPassword : AuthRoute
}
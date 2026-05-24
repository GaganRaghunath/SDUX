package com.anonymous.sdux.feature.auth.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anonymous.sdux.base.navigation.AppNavigator
import com.anonymous.sdux.base.navigation.FeatureNavGraph
import com.anonymous.sdux.feature.auth.navigation.AuthRoute
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles all [AuthRoute] destinations.
 *
 * Screen composables are placeholders — replace them with real
 * screen implementations as each screen is built out.
 */
@Singleton
class AuthNavGraph @Inject constructor() : FeatureNavGraph {

    override fun handles(route: Any): Boolean = route is AuthRoute

    @Composable
    override fun Destination(route: Any, navigator: AppNavigator) {
        when (route as AuthRoute) {
            AuthRoute.Login         -> LoginScreen(navigator)
            AuthRoute.Register      -> RegisterScreen(navigator)
            AuthRoute.ForgotPassword -> ForgotPasswordScreen(navigator)
        }
    }
}

// ── Screen placeholders — move to feature/auth/screen/ when implementing ──────

@Composable
private fun LoginScreen(navigator: AppNavigator) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Login Screen")
    }
}

@Composable
private fun RegisterScreen(navigator: AppNavigator) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Register Screen")
    }
}

@Composable
private fun ForgotPasswordScreen(navigator: AppNavigator) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Forgot Password Screen")
    }
}

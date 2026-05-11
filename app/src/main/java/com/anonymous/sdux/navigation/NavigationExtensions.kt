package com.anonymous.sdux.navigation

import androidx.navigation.NavController

fun NavController.safeNavigate(route: String) {
    try {
        navigate(route)
    } catch (e: Exception) {
        // Ignore duplicate navigation
    }
}

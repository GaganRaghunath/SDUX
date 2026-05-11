package com.anonymous.sdux.navigation

sealed class AppDestination(val route: String) {
    object Home : AppDestination("home")
    object Detail : AppDestination("detail/{itemId}") {
        fun createRoute(itemId: String) = "detail/$itemId"
    }
    object Settings : AppDestination("settings")
}

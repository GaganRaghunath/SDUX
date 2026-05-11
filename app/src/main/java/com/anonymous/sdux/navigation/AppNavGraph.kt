package com.anonymous.sdux.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anonymous.sdux.feature.detail.DetailScreen
import com.anonymous.sdux.feature.home.HomeScreen
import com.anonymous.sdux.feature.settings.SettingsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(
                onNavigateToDetail = { itemId ->
                    navController.safeNavigate(AppDestination.Detail.createRoute(itemId))
                },
                onNavigateToSettings = {
                    navController.safeNavigate(AppDestination.Settings.route)
                },
            )
        }
        composable(
            route = AppDestination.Detail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            DetailScreen(
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

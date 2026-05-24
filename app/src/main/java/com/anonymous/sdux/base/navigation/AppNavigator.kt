package com.anonymous.sdux.base.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * App-wide navigation contract.
 * Access inside any composable via [LocalAppNavigator].
 */
interface AppNavigator {

    /** Push [route] onto the back stack. */
    fun navigate(route: Any)

    /** Pop the top entry. No-op if already at root. */
    fun navigateBack()

    /**
     * Pop back to [route].
     * @param inclusive if true, [route] itself is also removed.
     */
    fun popTo(route: Any, inclusive: Boolean = false)

    /** Clear the entire stack and push [route] as the new root. */
    fun replaceAll(route: Any)
}

/** CompositionLocal that gives any composable access to [AppNavigator]. */
val LocalAppNavigator = compositionLocalOf<AppNavigator> {
    error("AppNavigator not provided. Ensure your composable is inside AppNavHost.")
}

// ── Internal implementation ───────────────────────────────────────────────────

internal class AppNavigatorImpl(
    private val backStack: NavBackStack<NavKey>
) : AppNavigator {

    override fun navigate(route: Any) {
        backStack.add(route as NavKey)
    }

    override fun navigateBack() {
        if (backStack.size > 1) backStack.removeLast()
    }

    override fun popTo(route: Any, inclusive: Boolean) {
        val index = backStack.indexOfLast { it == route }
        if (index < 0) return
        val removeFrom = if (inclusive) index else index + 1
        if (removeFrom < backStack.size) {
            backStack.subList(removeFrom, backStack.size).clear()
        }
    }

    override fun replaceAll(route: Any) {
        backStack.clear()
        backStack.add(route as NavKey)
    }
}

@Composable
internal fun rememberAppNavigator(backStack: NavBackStack<NavKey>): AppNavigator =
    remember(backStack) { AppNavigatorImpl(backStack) }

# Navigation 3

SDUX uses Jetpack Navigation 3 (`androidx.navigation3`, currently `1.0.0-alpha04`).

> Always verify the latest version at: `d.android.com/jetpack/androidx/releases/navigation3`

---

## Architecture Overview

```
MainActivity
    └── AppNavHost
            ├── rememberNavBackStack(AppRoute.Home)   ← SnapshotStateList<Any>
            ├── AppNavigatorImpl                       ← wraps back stack
            ├── LocalAppNavigator (CompositionLocal)   ← available to all composables
            └── NavDisplay { entry →
                    NavGraphRegistry.graphs
                        .first { it.handles(entry) }
                        .Destination(entry, navigator)
                }
```

---

## Key Files

| File | Role |
|---|---|
| `base/navigation/AppNavigator.kt` | `AppNavigator` interface + `LocalAppNavigator` + `AppNavigatorImpl` |
| `base/navigation/FeatureNavGraph.kt` | Contract every feature nav graph implements |
| `framework/navigation/AppRoute.kt` | Root sealed interface for all routes |
| `framework/navigation/AppNavHost.kt` | Root `NavDisplay` composable |
| `framework/navigation/NavGraphRegistry.kt` | Hilt-injectable set of all `FeatureNavGraph`s |
| `framework/di/NavigationModule.kt` | Hilt `@Multibinds` + `@Binds @IntoSet` per feature |

---

## Routes

All routes are plain Kotlin objects/data classes inside a `sealed interface` that extends `AppRoute`.

```
AppRoute (sealed interface)
├── AppRoute.Home
└── AuthRoute (sealed interface : AppRoute)
    ├── AuthRoute.Login
    ├── AuthRoute.Register
    └── AuthRoute.ForgotPassword
```

### Defining routes for a new feature

```kotlin
// feature/home/navigation/HomeRoute.kt
sealed interface HomeRoute : AppRoute {
    data object Feed   : HomeRoute
    data class  Post(val id: String) : HomeRoute
}
```

Rules:
- Use `data object` for screens with no arguments
- Use `data class` for screens that need typed parameters
- Never use `String` arguments — encode everything in the data class

---

## AppNavigator

Access from any composable inside `AppNavHost`:

```kotlin
val navigator = LocalAppNavigator.current

// Push a new destination
navigator.navigate(AuthRoute.Login)

// Go back (no-op if already at root)
navigator.navigateBack()

// Pop back to a specific destination
navigator.popTo(HomeRoute.Feed, inclusive = false)

// Clear the entire stack and start fresh
navigator.replaceAll(AuthRoute.Login)
```

---

## FeatureNavGraph

Each feature implements this interface and is injected into `NavGraphRegistry` via Hilt multibinding.

```kotlin
@Singleton
class HomeNavGraph @Inject constructor() : FeatureNavGraph {

    override fun handles(route: Any): Boolean = route is HomeRoute

    @Composable
    override fun Destination(route: Any, navigator: AppNavigator) {
        when (route as HomeRoute) {
            is HomeRoute.Feed -> FeedScreen(navigator)
            is HomeRoute.Post -> PostScreen(route.id, navigator)
        }
    }
}
```

---

## Registering a Feature

In `framework/di/NavigationModule.kt`, add one `@Binds @IntoSet` per feature:

```kotlin
@Binds @IntoSet
abstract fun bindHomeNavGraph(impl: HomeNavGraph): FeatureNavGraph
```

The `@Multibinds` declaration at the top of `NavigationModule` ensures the set is always provided even when empty.

---

## AppNavHost Internals

```
1. rememberNavBackStack(startRoute)
       → SnapshotStateList<Any> (Navigation 3's back stack)

2. AppNavigatorImpl(backStack)
       → implements AppNavigator using MutableList operations

3. CompositionLocalProvider(LocalAppNavigator provides navigator)
       → makes navigator available deep in the composable tree

4. NavDisplay(backStack, onBack) { entry →
       registry.graphs.first { it.handles(entry) }.Destination(entry, navigator)
   }
       → renders only the top-of-stack entry
       → dispatches to the correct FeatureNavGraph on every recomposition
```

The `@Suppress("UNCHECKED_CAST")` on the backStack cast is safe: JVM erases generic types at runtime, and we only ever push `AppRoute` subtypes onto the stack.

---

## Step-by-Step: Adding a New Navigation Destination

1. Create `feature/x/navigation/XRoute.kt` — `sealed interface XRoute : AppRoute`
2. Create `feature/x/navigation/XNavGraph.kt` — `class XNavGraph @Inject constructor() : FeatureNavGraph`
3. Add `@Binds @IntoSet abstract fun bindXNavGraph(impl: XNavGraph): FeatureNavGraph` to `NavigationModule.kt`
4. Navigate to it from anywhere with `LocalAppNavigator.current.navigate(XRoute.SomeDestination)`

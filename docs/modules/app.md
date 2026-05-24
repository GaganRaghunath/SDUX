# :app Module

The presentation layer. Contains all UI, navigation, ViewModels, and Android framework wiring.

---

## Folder Structure

```
app/src/main/java/com/anonymous/sdux/
│
├── SDUXApplication.kt          @HiltAndroidApp + WorkManager config
├── MainActivity.kt             @AndroidEntryPoint, entry point for Compose
│
├── base/                       Abstract templates every feature extends
│   ├── model/
│   │   └── BaseUiModel.kt      Marker interface for all UI models
│   ├── navigation/
│   │   ├── AppNavigator.kt     Navigation interface + LocalAppNavigator + impl
│   │   ├── FeatureNavGraph.kt  Contract each feature nav graph implements
│   │   └── NavDestination.kt   Legacy route string contract (Nav 2 remnant)
│   ├── state/
│   │   ├── BaseUiState.kt      interface { isLoading, error }
│   │   └── BaseUiEvent.kt      Marker interface for one-shot events
│   ├── ui/
│   │   ├── BaseActivity.kt     ComponentActivity + edge-to-edge + SDUXTheme
│   │   └── BaseScreen.kt       Loading overlay + error snackbar composable
│   └── viewmodel/
│       └── BaseViewModel.kt    StateFlow state + Channel events
│
├── feature/                    One folder per product feature
│   └── auth/                   ← template; replicate for every new feature
│       ├── component/          Feature-scoped reusable composables
│       ├── model/              AuthUiModel : BaseUiModel
│       ├── navigation/
│       │   ├── AuthRoute.kt    sealed interface AuthRoute : AppRoute
│       │   └── AuthNavGraph.kt AuthNavGraph : FeatureNavGraph (Hilt @Singleton)
│       ├── screen/             AuthScreen.kt composable entry point
│       ├── state/              AuthUiState, AuthUiEvent
│       └── viewmodel/          AuthViewModel : BaseViewModel<AuthUiState, AuthUiEvent>
│
├── framework/                  Android/3rd-party wiring (not business logic)
│   ├── analytics/              Analytics tracker setup
│   ├── di/
│   │   ├── AppModule.kt        App-wide @Provides (DispatchersModule lives here)
│   │   ├── NavigationModule.kt @Multibinds + @Binds @IntoSet per FeatureNavGraph
│   │   └── qualifiers/
│   │       └── CoroutineQualifiers.kt  @IoDispatcher, @DefaultDispatcher, @MainDispatcher
│   ├── logging/                Timber setup
│   ├── navigation/
│   │   ├── AppRoute.kt         sealed interface AppRoute { Home }
│   │   ├── NavGraphRegistry.kt @Singleton holding Set<FeatureNavGraph>
│   │   └── AppNavHost.kt       Root NavDisplay composable
│   ├── network/                App-level network monitoring
│   ├── notification/           Push/local notification channels
│   └── storage/                DataStore / SharedPreferences wrappers
│
├── ui/theme/                   Material3 theme (Color, Theme, Type)
│
└── util/                       Stateless helpers
    ├── connectivity/           Network state observer
    ├── extension/              Kotlin/Compose extension functions
    ├── formatter/              Date, number, string formatters
    ├── mapper/                 UiModel ↔ DomainModel mappers
    └── permission/             Runtime permission helpers
```

---

## Base Class Reference

### `BaseViewModel<State, Event>`

```kotlin
// Extend for every feature ViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<AuthUiState, AuthUiEvent>(AuthUiState()) {

    fun login(email: String, password: String) = launch {
        updateState { copy(isLoading = true) }
        loginUseCase(LoginParams(email, password))
            .onSuccess { sendEvent(AuthUiEvent.NavigateHome) }
            .onError   { updateState { copy(isLoading = false, error = it.message) } }
    }
}
```

Key members:
- `uiState: StateFlow<State>` — collect in composable with `collectAsStateWithLifecycle()`
- `events: Flow<Event>` — one-shot UI events (navigate, toast)
- `updateState { }` — copies current state with lambda
- `sendEvent(event)` — pushes to the event channel
- `launch { }` — scoped to `viewModelScope`

---

### `BaseUiState`

```kotlin
data class AuthUiState(
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val user: AuthUiModel? = null          // feature-specific fields
) : BaseUiState
```

---

### `BaseScreen`

```kotlin
@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                AuthUiEvent.NavigateHome -> navigator.navigate(AppRoute.Home)
            }
        }
    }

    BaseScreen(state = state, snackbarHostState = snackbarHostState) { s ->
        // Your screen content; loading and error handled by BaseScreen
    }
}
```

---

### `BaseActivity`

`MainActivity` extends `BaseActivity` and only implements `Content()`. `BaseActivity` owns `enableEdgeToEdge()`, `setContent {}`, and `SDUXTheme {}`.

---

## Adding a New Feature

1. Copy the `feature/auth/` folder structure, rename all occurrences of `Auth` → `YourFeature`
2. Define `YourFeatureRoute : AppRoute`
3. Implement `YourFeatureNavGraph : FeatureNavGraph`
4. Register in `NavigationModule.kt` with `@Binds @IntoSet`
5. Create `YourFeatureUiState`, `YourFeatureUiEvent`
6. Create `YourFeatureViewModel : BaseViewModel<YourFeatureUiState, YourFeatureUiEvent>`
7. Build `YourFeatureScreen` using `BaseScreen`

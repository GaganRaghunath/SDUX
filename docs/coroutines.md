# Coroutines

SDUX uses Kotlin Coroutines 1.9.0 throughout. Each module has a defined role.

---

## Dispatcher Strategy

| Dispatcher | Qualifier | Where it runs |
|---|---|---|
| `Dispatchers.IO` | `@IoDispatcher` | Use cases (network, disk) |
| `Dispatchers.Default` | `@DefaultDispatcher` | CPU-intensive transforms |
| `Dispatchers.Main` | `@MainDispatcher` | Rarely needed; StateFlow handles UI |

Dispatchers are never hardcoded — they are injected via the qualifier annotations defined in `framework/di/qualifiers/CoroutineQualifiers.kt`. This makes use cases fully testable with a `TestCoroutineDispatcher`.

---

## Use Case Layer (`:domain`)

The dispatcher is set at the use case level so ViewModels never touch it:

```kotlin
// UseCase.kt
abstract class UseCase<in Params, out Result>(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(params: Params): DomainResult<Result> =
        withContext(dispatcher) { execute(params) }

    protected abstract suspend fun execute(params: Params): DomainResult<Result>
}
```

`FlowUseCase` applies `flowOn(dispatcher)` on the returned flow:

```kotlin
operator fun invoke(params: Params): Flow<DomainResult<Result>> =
    execute(params).flowOn(dispatcher)
```

---

## Repository Layer (`:data`)

`BaseRepository` provides two coroutine helpers:

```kotlin
// Single-shot (Loading state NOT emitted)
protected suspend fun <T> apiCall(call: suspend () -> T): DomainResult<T> =
    safeApiCall(call)

// Streaming (emits Loading → then Success/Error)
protected fun <T> flowApiCall(call: suspend () -> T): Flow<DomainResult<T>> = flow {
    emit(DomainResult.Loading)
    emit(safeApiCall(call))
}
```

Use `apiCall` when the ViewModel drives the loading state.  
Use `flowApiCall` when you want loading built into the stream.

---

## ViewModel Layer (`:app`)

`BaseViewModel` exposes a `launch` helper scoped to `viewModelScope`:

```kotlin
fun login(email: String, password: String) = launch {
    // Runs on Main; use case switches to IO internally
    updateState { copy(isLoading = true) }
    loginUseCase(LoginParams(email, password))
        .onSuccess { user ->
            updateState { copy(isLoading = false, user = user.toUiModel()) }
            sendEvent(AuthUiEvent.NavigateHome)
        }
        .onError { ex ->
            updateState { copy(isLoading = false, error = ex.message) }
        }
}
```

State changes via `updateState { }` are always safe to call from any coroutine — `MutableStateFlow.update` is thread-safe.

---

## Collecting State in Composables

```kotlin
@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    // Lifecycle-aware collection — pauses when app is backgrounded
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // One-shot events via Channel — never missed even on recomposition
    val navigator = LocalAppNavigator.current
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AuthUiEvent.NavigateHome -> navigator.replaceAll(AppRoute.Home)
                is AuthUiEvent.ShowToast   -> { /* show snackbar */ }
            }
        }
    }
}
```

`collectAsStateWithLifecycle()` comes from `lifecycle-runtime-compose` (already in the BOM bundle).

---

## Testing Use Cases

Inject `UnconfinedTestDispatcher` to make tests synchronous:

```kotlin
class LoginUseCaseTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun `returns success on valid credentials`() = runTest {
        val useCase = LoginUseCase(fakeRepo, dispatcher)
        val result = useCase(LoginParams("a@b.com", "password"))
        assertThat(result).isInstanceOf(DomainResult.Success::class.java)
    }
}
```

`kotlinx-coroutines-test` is declared in `libs.bundles.testing` and available in all three modules.

---

## Error Handling Flow

```
Retrofit call throws
        │
        ▼
safeApiCall (data layer)
        │  maps to DomainException
        ▼
DomainResult.Error(exception)
        │  returned by use case
        ▼
ViewModel.onError { ex →
    updateState { copy(error = ex.message) }
}
        │  state update
        ▼
BaseScreen shows error Snackbar
```

No `try/catch` in ViewModels or composables — all errors are channelled through `DomainResult`.

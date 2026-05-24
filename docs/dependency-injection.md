# Dependency Injection — Hilt

SDUX uses Hilt 2.52 with KSP 2.3.4 for annotation processing.

---

## Component Scope Map

| Scope annotation | Component | Lifetime |
|---|---|---|
| `@Singleton` | `SingletonComponent` | App process lifetime |
| `@ActivityRetainedScoped` | `ActivityRetainedComponent` | Survives rotation |
| `@ViewModelScoped` | `ViewModelComponent` | ViewModel lifetime |
| `@ActivityScoped` | `ActivityComponent` | Activity lifetime |

Use `@Singleton` for repositories, use cases (stateless), and nav graphs.  
Use `@ViewModelScoped` for objects tied to one ViewModel.

---

## Entry Points

| Class | Annotation | Notes |
|---|---|---|
| `SDUXApplication` | `@HiltAndroidApp` | Required on the Application |
| `MainActivity` | `@AndroidEntryPoint` | Enables field injection |
| Any `@HiltViewModel` | `@HiltViewModel` | Injected via `hiltViewModel()` |
| `HiltWorker` | `@HiltWorker` | Injected via `HiltWorkerFactory` |

---

## Module Files

### `framework/di/AppModule.kt`

```
AppModule          — placeholder for app-wide @Provides (OkHttp, Gson, etc.)
DispatchersModule  — provides @IoDispatcher, @DefaultDispatcher, @MainDispatcher
```

### `framework/di/NavigationModule.kt`

```
NavigationModule   — @Multibinds Set<FeatureNavGraph>
                   — @Binds @IntoSet per FeatureNavGraph
```

### `data/di/DataModule.kt`

```
DataModule         — @Binds per repository interface → implementation
```

---

## Dispatcher Qualifiers

Named coroutine dispatchers prevent accidental use of the wrong dispatcher:

```kotlin
// Inject in use cases
class LoginUseCase @Inject constructor(
    private val repo: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UseCase<LoginParams, User>(dispatcher)

// Inject directly when needed
class MyViewModel @Inject constructor(
    @MainDispatcher private val main: CoroutineDispatcher
) : ViewModel()
```

Available qualifiers (defined in `framework/di/qualifiers/CoroutineQualifiers.kt`):

| Annotation | Dispatcher | Use for |
|---|---|---|
| `@IoDispatcher` | `Dispatchers.IO` | Network, disk, database |
| `@DefaultDispatcher` | `Dispatchers.Default` | CPU-intensive work |
| `@MainDispatcher` | `Dispatchers.Main` | UI updates (rare — prefer StateFlow) |

---

## ViewModel Injection

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<AuthUiState, AuthUiEvent>(AuthUiState())

// In composable
@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) { ... }
```

---

## Repository Binding Pattern

Always bind interface → implementation with `@Binds` (not `@Provides`):

```kotlin
// In DataModule.kt
@Binds
abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

// In AuthRepositoryImpl.kt
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val dao: UserDao
) : BaseRepository(), AuthRepository
```

---

## Hilt Multibinding — Nav Graphs

`Set<FeatureNavGraph>` is assembled by Hilt at compile time from all `@Binds @IntoSet` declarations. Adding a new feature requires one line in `NavigationModule.kt`:

```kotlin
@Binds @IntoSet
abstract fun bindHomeNavGraph(impl: HomeNavGraph): FeatureNavGraph
```

`NavGraphRegistry` (a `@Singleton`) receives the complete set via its `@Inject constructor`.

---

## WorkManager + Hilt

`SDUXApplication` disables automatic WorkManager initialization and provides `HiltWorkerFactory`:

```kotlin
@HiltAndroidApp
class SDUXApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    override val workManagerConfiguration get() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()
}
```

`AndroidManifest.xml` removes `WorkManagerInitializer` from the Startup library to prevent double-initialization.

Workers use `@HiltWorker` and `@AssistedInject`:

```kotlin
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: SyncRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result { ... }
}
```

---

## Adding a New DI Module

1. Create `framework/di/FeatureModule.kt` (or `data/di/FeatureDataModule.kt` for data bindings)
2. Annotate with `@Module` + `@InstallIn(SingletonComponent::class)` (or appropriate component)
3. Use `@Provides` for object construction, `@Binds` for interface → implementation

No changes to the root build file needed — Hilt aggregates all `@Module` classes at compile time.

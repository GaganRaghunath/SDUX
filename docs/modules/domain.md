# :domain Module

The business logic layer. Pure Kotlin — no Android SDK imports, no framework dependencies.

---

## Folder Structure

```
domain/src/main/java/com/anonymous/domain/
│
├── base/                        Abstract templates use-case components extend
│   ├── model/
│   │   └── DomainModel.kt       Marker interface for all domain models
│   ├── result/                  (empty — base result helpers go here)
│   ├── usecase/
│   │   ├── UseCase.kt           suspend, 1 param, returns DomainResult<R>
│   │   ├── FlowUseCase.kt       returns Flow<DomainResult<R>>, uses flowOn(IO)
│   │   └── NoParamUseCase.kt    suspend, no param, returns DomainResult<R>
│   └── validation/
│       └── Validator.kt         abstract validate(input): ValidationResult
│                                infix `and` chains two validators
│
├── common/                      Shared concrete types used across all features
│   ├── exception/
│   │   └── DomainException.kt   Sealed: Network, Unauthorized, NotFound,
│   │                             Validation, Server, Unknown
│   ├── result/
│   │   └── DomainResult.kt      Sealed: Success, Error, Loading
│   │                             + onSuccess/onError/onLoading/map/getOrElse
│   └── validation/
│       └── ValidationResult.kt  Sealed: Valid, Invalid(errors)
│                                 + plus() operator to merge results
│
└── auth/                        ← feature template (replicate per feature)
    ├── model/                   Auth domain models : DomainModel
    ├── repository/              AuthRepository interface
    ├── result/                  Auth-specific result types
    ├── usecase/                 LoginUseCase, LogoutUseCase, etc.
    └── validation/              EmailValidator, PasswordValidator, etc.
```

---

## Use Case Types

### `UseCase<Params, Result>` — single shot, with params

```kotlin
class LoginUseCase @Inject constructor(
    private val repo: AuthRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher  // injected by Hilt
) : UseCase<LoginParams, User>(dispatcher) {

    override suspend fun execute(params: LoginParams): DomainResult<User> =
        repo.login(params.email, params.password)
}
```

### `NoParamUseCase<Result>` — single shot, no params

```kotlin
class GetCurrentUserUseCase @Inject constructor(
    private val repo: AuthRepository
) : NoParamUseCase<User>() {

    override suspend fun execute(): DomainResult<User> =
        repo.getCurrentUser()
}
```

### `FlowUseCase<Params, Result>` — streaming

```kotlin
class ObserveUserUseCase @Inject constructor(
    private val repo: AuthRepository
) : FlowUseCase<String, User>() {

    override fun execute(params: String): Flow<DomainResult<User>> =
        repo.observeUser(params)
}
```

---

## DomainResult

```kotlin
// Consuming in a ViewModel
loginUseCase(params)
    .onSuccess { user -> updateState { copy(user = user.toUiModel()) } }
    .onError   { ex  -> updateState { copy(error = ex.message) } }
    .onLoading {       updateState { copy(isLoading = true) } }

// Transforming
val nameResult: DomainResult<String> = userResult.map { it.name }

// Unwrapping safely
val name: String = nameResult.getOrElse { "Guest" }
```

---

## DomainException Hierarchy

```
DomainException
├── NetworkException(message, cause)
├── UnauthorizedException(message)
├── NotFoundException(message)
├── ValidationException(errors: List<String>)
├── ServerException(code, message)
└── UnknownException(message, cause)
```

Map HTTP errors to these in `safeApiCall` (`:data`). ViewModels read `exception.message` or switch on subtype for tailored error messages.

---

## Validation

```kotlin
// Define validators
class EmailValidator : Validator<String>() {
    override fun validate(input: String): ValidationResult =
        if (input.contains("@")) ValidationResult.Valid
        else ValidationResult.Invalid("Invalid email format")
}

class PasswordValidator : Validator<String>() {
    override fun validate(input: String): ValidationResult =
        if (input.length >= 8) ValidationResult.Valid
        else ValidationResult.Invalid("Password must be at least 8 characters")
}

// Chain with infix `and`
val validator = EmailValidator() and PasswordValidator()
val result = validator.validate(input)  // combines both ValidationResults
```

---

## Adding a Feature to Domain

1. Create `domain/feature-name/model/FeatureModel.kt` implementing `DomainModel`
2. Create `domain/feature-name/repository/FeatureRepository.kt` (interface only)
3. Create use cases in `domain/feature-name/usecase/` extending the right base class
4. Add validators in `domain/feature-name/validation/` if input validation is needed
5. Add feature-specific result/state types in `domain/feature-name/result/` if needed

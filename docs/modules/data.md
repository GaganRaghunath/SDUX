# :data Module

The data layer. Implements domain repository interfaces and manages all I/O (network + local DB).

---

## Folder Structure

```
data/src/main/java/com/anonymous/data/
│
├── di/
│   └── DataModule.kt            Hilt @Module — binds repository interfaces to impls
│
├── local/
│   ├── dao/                     Room DAO interfaces
│   ├── database/                Room Database class
│   └── entity/
│       └── BaseEntity.kt        Marker interface for all Room entities
│
├── mapper/
│   └── BaseMapper.kt            BaseMapper<From,To>, BiMapper<A,B>, mapList()
│
├── remote/
│   ├── api/
│   │   └── SafeApiCall.kt       safeApiCall { } — wraps Retrofit → DomainResult
│   ├── dto/
│   │   └── BaseDto.kt           Marker interface for all API response DTOs
│   └── interceptor/             OkHttp interceptors (auth header, logging)
│
└── repository/
    └── BaseRepository.kt        apiCall { } and flowApiCall { } helpers
```

---

## BaseRepository

All repository implementations extend `BaseRepository`:

```kotlin
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val dao: UserDao
) : BaseRepository(), AuthRepository {

    override suspend fun login(email: String, pass: String): DomainResult<User> =
        apiCall { api.login(LoginRequest(email, pass)) }
            .map { it.toDomain() }   // DTO → DomainModel

    override fun observeUser(id: String): Flow<DomainResult<User>> =
        flowApiCall { api.getUser(id) }
            .map { result -> result.map { it.toDomain() } }
}
```

`apiCall { }` — wraps a suspend Retrofit call in `safeApiCall`, returns `DomainResult<T>`  
`flowApiCall { }` — emits `Loading` then the result as a `Flow<DomainResult<T>>`

---

## SafeApiCall

`safeApiCall` is a free function in `remote/api/SafeApiCall.kt` that maps HTTP/IO errors to `DomainException`:

| Exception type | Maps to |
|---|---|
| `HttpException(401)` | `DomainException.UnauthorizedException` |
| `HttpException(404)` | `DomainException.NotFoundException` |
| `HttpException(5xx)` | `DomainException.ServerException(code)` |
| `IOException` | `DomainException.NetworkException` |
| Any other | `DomainException.UnknownException` |

---

## Mappers

```kotlin
// DTO → Domain model
class UserDtoMapper : BaseMapper<UserDto, User> {
    override fun map(from: UserDto): User =
        User(id = from.id, name = from.name, email = from.email)
}

// Entity ↔ Domain model (bidirectional)
class UserEntityMapper : BiMapper<UserEntity, User> {
    override fun map(from: UserEntity): User = User(from.id, from.name, from.email)
    override fun reverseMap(from: User): UserEntity = UserEntity(from.id, from.name, from.email)
}

// Map a list
val users: List<User> = userDtoMapper.mapList(dtoList)
```

---

## DataModule

Bind each repository interface → implementation with `@Binds`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    // Add one @Binds per feature repository as features are built
}
```

`@Binds` is preferred over `@Provides` for interface→implementation bindings: no object instantiation, zero overhead.

---

## Adding a Feature to Data

1. Create `remote/dto/FeatureDto.kt` implementing `BaseDto` — matches the API JSON shape
2. Create `local/entity/FeatureEntity.kt` implementing `BaseEntity` — matches the Room table
3. Create `local/dao/FeatureDao.kt` — Room DAO interface
4. Create `mapper/FeatureMapper.kt` extending `BaseMapper` or `BiMapper`
5. Create `repository/FeatureRepositoryImpl.kt` extending `BaseRepository`, implementing `:domain`'s `FeatureRepository`
6. Register the binding in `DataModule.kt`

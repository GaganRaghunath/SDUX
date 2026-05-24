# Guide: Adding a New Feature

End-to-end checklist for adding a new feature to SDUX. Use `auth` as the reference implementation.

---

## Overview

A complete feature touches three modules across seven layers:

```
:domain  →  model · repository interface · use case · validation
:data    →  dto · entity · mapper · repository implementation · DI binding
:app     →  route · nav graph · ui model · state · event · viewmodel · screen
```

---

## Step 1 — Domain Model

**File:** `:domain` → `feature-name/model/FeatureModel.kt`

```kotlin
data class Product(
    val id: String,
    val name: String,
    val price: Double
) : DomainModel
```

Rules:
- Implement `DomainModel` (marker interface)
- No Android imports, no JSON annotations
- Immutable — always `data class` or `data object`

---

## Step 2 — Repository Interface

**File:** `:domain` → `feature-name/repository/FeatureRepository.kt`

```kotlin
interface ProductRepository {
    suspend fun getProduct(id: String): DomainResult<Product>
    fun observeProducts(): Flow<DomainResult<List<Product>>>
}
```

Rules:
- Interface only — no implementation here
- Return `DomainResult<T>` for single-shot, `Flow<DomainResult<T>>` for streaming
- The implementation lives in `:data`

---

## Step 3 — Use Cases

**File:** `:domain` → `feature-name/usecase/`

```kotlin
// Single-shot with param
class GetProductUseCase @Inject constructor(
    private val repo: ProductRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : UseCase<String, Product>(dispatcher) {
    override suspend fun execute(params: String): DomainResult<Product> =
        repo.getProduct(params)
}

// No param
class GetAllProductsUseCase @Inject constructor(
    private val repo: ProductRepository
) : NoParamUseCase<List<Product>>() {
    override suspend fun execute(): DomainResult<List<Product>> =
        repo.getAllProducts()
}

// Streaming
class ObserveProductsUseCase @Inject constructor(
    private val repo: ProductRepository
) : FlowUseCase<Unit, List<Product>>() {
    override fun execute(params: Unit): Flow<DomainResult<List<Product>>> =
        repo.observeProducts()
}
```

---

## Step 4 — Validation (if needed)

**File:** `:domain` → `feature-name/validation/`

```kotlin
class ProductNameValidator : Validator<String>() {
    override fun validate(input: String): ValidationResult =
        if (input.isNotBlank() && input.length <= 100) ValidationResult.Valid
        else ValidationResult.Invalid("Product name must be 1–100 characters")
}
```

Chain validators with `and`:
```kotlin
val validator = ProductNameValidator() and ProductPriceValidator()
```

---

## Step 5 — DTO

**File:** `:data` → `remote/dto/FeatureDto.kt`

```kotlin
@Serializable  // or @JsonClass(generateAdapter = true) for Moshi
data class ProductDto(
    @SerialName("product_id") val id: String,
    @SerialName("product_name") val name: String,
    @SerialName("price") val price: Double
) : BaseDto
```

---

## Step 6 — Room Entity (if persisting locally)

**File:** `:data` → `local/entity/FeatureEntity.kt`

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double
) : BaseEntity
```

---

## Step 7 — Mapper

**File:** `:data` → `mapper/FeatureMapper.kt`

```kotlin
// DTO → Domain
class ProductDtoMapper : BaseMapper<ProductDto, Product> {
    override fun map(from: ProductDto): Product =
        Product(id = from.id, name = from.name, price = from.price)
}

// Entity ↔ Domain
class ProductEntityMapper : BiMapper<ProductEntity, Product> {
    override fun map(from: ProductEntity): Product =
        Product(from.id, from.name, from.price)
    override fun reverseMap(from: Product): ProductEntity =
        ProductEntity(from.id, from.name, from.price)
}
```

---

## Step 8 — Repository Implementation

**File:** `:data` → `repository/FeatureRepositoryImpl.kt`

```kotlin
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService,
    private val dao: ProductDao,
    private val dtoMapper: ProductDtoMapper,
    private val entityMapper: ProductEntityMapper
) : BaseRepository(), ProductRepository {

    override suspend fun getProduct(id: String): DomainResult<Product> =
        apiCall { api.getProduct(id) }.map { dtoMapper.map(it) }

    override fun observeProducts(): Flow<DomainResult<List<Product>>> =
        flowApiCall { api.getProducts() }
            .map { result -> result.map { dtoMapper.mapList(it) } }
}
```

---

## Step 9 — Bind Repository in DataModule

**File:** `:data` → `di/DataModule.kt`

```kotlin
@Binds
abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
```

---

## Step 10 — Routes

**File:** `:app` → `feature/feature-name/navigation/FeatureRoute.kt`

```kotlin
sealed interface ProductRoute : AppRoute {
    data object List              : ProductRoute
    data class  Detail(val id: String) : ProductRoute
    data object AddNew            : ProductRoute
}
```

---

## Step 11 — Nav Graph

**File:** `:app` → `feature/feature-name/navigation/FeatureNavGraph.kt`

```kotlin
@Singleton
class ProductNavGraph @Inject constructor() : FeatureNavGraph {

    override fun handles(route: Any): Boolean = route is ProductRoute

    @Composable
    override fun Destination(route: Any, navigator: AppNavigator) {
        when (route as ProductRoute) {
            ProductRoute.List          -> ProductListScreen(navigator)
            is ProductRoute.Detail     -> ProductDetailScreen(route.id, navigator)
            ProductRoute.AddNew        -> AddProductScreen(navigator)
        }
    }
}
```

---

## Step 12 — Register in NavigationModule

**File:** `:app` → `framework/di/NavigationModule.kt`

```kotlin
@Binds @IntoSet
abstract fun bindProductNavGraph(impl: ProductNavGraph): FeatureNavGraph
```

---

## Step 13 — UI Model

**File:** `:app` → `feature/feature-name/model/FeatureUiModel.kt`

```kotlin
data class ProductUiModel(
    val id: String,
    val displayName: String,
    val formattedPrice: String      // "$12.99" — formatting done in mapper
) : BaseUiModel

// Mapper extension (in util/mapper/ or feature/model/)
fun Product.toUiModel() = ProductUiModel(
    id             = id,
    displayName    = name,
    formattedPrice = "$${"%.2f".format(price)}"
)
```

---

## Step 14 — UiState and UiEvent

**File:** `:app` → `feature/feature-name/state/`

```kotlin
data class ProductListUiState(
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val products: List<ProductUiModel> = emptyList()
) : BaseUiState

sealed interface ProductListUiEvent : BaseUiEvent {
    data class NavigateToDetail(val id: String) : ProductListUiEvent
    data object NavigateToAdd : ProductListUiEvent
}
```

---

## Step 15 — ViewModel

**File:** `:app` → `feature/feature-name/viewmodel/FeatureViewModel.kt`

```kotlin
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getAllProducts: GetAllProductsUseCase
) : BaseViewModel<ProductListUiState, ProductListUiEvent>(ProductListUiState()) {

    init { loadProducts() }

    private fun loadProducts() = launch {
        updateState { copy(isLoading = true) }
        getAllProducts()
            .onSuccess { products ->
                updateState { copy(isLoading = false, products = products.map { it.toUiModel() }) }
            }
            .onError { ex ->
                updateState { copy(isLoading = false, error = ex.message) }
            }
    }

    fun onProductClick(id: String) = sendEvent(ProductListUiEvent.NavigateToDetail(id))
}
```

---

## Step 16 — Screen

**File:** `:app` → `feature/feature-name/screen/FeatureScreen.kt`

```kotlin
@Composable
fun ProductListScreen(
    navigator: AppNavigator,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProductListUiEvent.NavigateToDetail ->
                    navigator.navigate(ProductRoute.Detail(event.id))
                ProductListUiEvent.NavigateToAdd ->
                    navigator.navigate(ProductRoute.AddNew)
            }
        }
    }

    BaseScreen(state = state, snackbarHostState = snackbarHostState) { s ->
        LazyColumn {
            items(s.products) { product ->
                ProductItem(product, onClick = { viewModel.onProductClick(product.id) })
            }
        }
    }
}
```

---

## Checklist Summary

```
:domain
  [ ] model/FeatureModel.kt          implements DomainModel
  [ ] repository/FeatureRepository.kt  interface
  [ ] usecase/GetFeatureUseCase.kt   extends UseCase / FlowUseCase / NoParamUseCase
  [ ] validation/FeatureValidator.kt  extends Validator (if needed)

:data
  [ ] remote/dto/FeatureDto.kt       implements BaseDto
  [ ] local/entity/FeatureEntity.kt  implements BaseEntity (if local cache)
  [ ] mapper/FeatureMapper.kt        extends BaseMapper / BiMapper
  [ ] repository/FeatureRepositoryImpl.kt extends BaseRepository, implements FeatureRepository
  [ ] di/DataModule.kt               add @Binds for new repo

:app
  [ ] feature/x/navigation/XRoute.kt           sealed interface : AppRoute
  [ ] feature/x/navigation/XNavGraph.kt         implements FeatureNavGraph
  [ ] framework/di/NavigationModule.kt          add @Binds @IntoSet for nav graph
  [ ] feature/x/model/XUiModel.kt               implements BaseUiModel + toUiModel() mapper
  [ ] feature/x/state/XUiState.kt               implements BaseUiState
  [ ] feature/x/state/XUiEvent.kt               implements BaseUiEvent
  [ ] feature/x/viewmodel/XViewModel.kt         extends BaseViewModel<XUiState, XUiEvent>
  [ ] feature/x/screen/XScreen.kt               uses BaseScreen + hiltViewModel()
```

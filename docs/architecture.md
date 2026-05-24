# Architecture

SDUX follows **Clean Architecture** split across three Gradle modules. Each module maps to exactly one layer.

---

## Module Dependency Graph

```
        ┌──────────────────────────────┐
        │            :app              │
        │  (Presentation / UI layer)   │
        └──────────┬──────────┬───────┘
                   │          │
                   ▼          ▼
        ┌──────────────┐  ┌──────────────┐
        │   :domain    │◄─│    :data     │
        │ (Business    │  │ (Data layer) │
        │   logic)     │  └──────────────┘
        └──────────────┘
```

- `:app` depends on `:domain` (ViewModels call use cases) and `:data` (for Hilt bindings)
- `:data` depends on `:domain` (implements repository interfaces, maps to domain models)
- `:domain` depends on **nothing** — it is the stable center


---

## Layer Responsibilities

### `:domain` — Business Logic

- **Must not** import Android SDK classes (no `Context`, no `View`)
- **Must not** depend on `:data` or `:app`
- Contains: Use cases, domain models, repository *interfaces*, validation logic, `DomainResult`
- All I/O runs on `Dispatchers.IO` injected via constructor — domain is testable with no Android runner

### `:data` — Data Sources

- **Must not** import Compose or ViewModel classes
- **Must not** contain business logic — only data mapping and persistence
- Contains: Repository *implementations*, DTOs, Room entities, Retrofit services, mappers
- Depends on `:domain` to implement its repository contracts and return domain models

### `:app` — Presentation

- Depends on `:domain` (injects and calls use cases)
- May depend on `:data` for Hilt bindings only (never calls `data` classes directly from UI)
- Contains: ViewModels, Composable screens, Navigation, Hilt entry points, theme

---

## Data Flow

```
 Remote API / Room DB
        │
        ▼
   DTO / Entity          ← :data/remote/dto/, :data/local/entity/
        │  map()
        ▼
  Domain Model           ← :domain/feature/model/
        │  returned by UseCase
        ▼
    UiModel              ← :app/feature/model/
        │  held in UiState
        ▼
  Composable Screen      ← :app/feature/screen/
```

---

## Layer Rules — Quick Reference

| Rule | Reason |
|---|---|
| Domain models never reach the UI directly | UiModels decouple screen from business model changes |
| Repository interfaces live in `:domain` | Domain drives the contract; data fulfils it |
| `@Inject constructor` on use cases and repos | Keeps DI wiring minimal and testable |
| ViewModels never call repository directly | Always through a use case |
| No `suspend` in `FeatureNavGraph` | Navigation is a UI concern, not async |
| `Dispatchers.IO` set at the use case level | Screens and ViewModels stay on `Main` |

---

## Package Naming

```
com.anonymous.sdux      ← :app root
com.anonymous.domain    ← :domain root
com.anonymous.data      ← :data root
```

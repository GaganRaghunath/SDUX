# SDUX — Server-Driven UX

A modular Android application built on Clean Architecture, Jetpack Compose, and Navigation 3.

---

## Tech Stack

| Layer | Library | Version |
|---|---|---|
| Language | Kotlin | 2.2.10 |
| Build | AGP | 9.1.1 |
| Annotation processing | KSP | 2.3.4 |
| UI | Jetpack Compose BOM | 2026.02.01 |
| Navigation | Navigation 3 (alpha) | 1.0.0-alpha04 |
| DI | Hilt | 2.52 |
| Async | Coroutines | 1.9.0 |
| Lifecycle | AndroidX Lifecycle | 2.10.0 |
| Background work | WorkManager | 2.10.0 |
| Min SDK | — | 30 |
| Compile SDK | — | 36 |

---

## Modules

```
SDUX/
├── :app      — Presentation layer (Compose UI, Navigation, ViewModels)
├── :domain   — Business logic (Use Cases, Domain Models, Repository interfaces)
└── :data     — Data layer (Repository implementations, DTOs, Room, Retrofit)
```

Inter-module Gradle wiring:
- `:data` → `implementation(project(":domain"))`
- `:app` → `implementation(project(":domain"))` + `implementation(project(":data"))`

---

## Documentation Index

| Doc | What it covers |
|---|---|
| [architecture.md](architecture.md) | Layer rules, module boundaries, data flow |
| [modules/app.md](modules/app.md) | `:app` folder structure, base classes, feature pattern |
| [modules/domain.md](modules/domain.md) | `:domain` use cases, models, validation, results |
| [modules/data.md](modules/data.md) | `:data` repositories, DTOs, mappers, Room, Retrofit |
| [navigation.md](navigation.md) | Navigation 3 setup, routes, adding destinations |
| [dependency-injection.md](dependency-injection.md) | Hilt setup, scopes, multibinding, WorkManager |
| [coroutines.md](coroutines.md) | Dispatcher qualifiers, use case patterns, testing |
| [guides/new-feature.md](guides/new-feature.md) | End-to-end checklist for adding a new feature |

---

## Quick Start — Adding a Feature

1. Add domain models + repository interface in `:domain/feature-name/`
2. Add use cases extending `UseCase` / `FlowUseCase` in `:domain/feature-name/usecase/`
3. Implement the repository in `:data/repository/`, add DTOs and mappers
4. Bind the implementation in `DataModule.kt`
5. Define routes as a `sealed interface XRoute : AppRoute`
6. Implement `FeatureNavGraph`, register in `NavigationModule.kt`
7. Create `XViewModel : BaseViewModel<XState, XEvent>`
8. Build the screen composable using `BaseScreen`

Full checklist → [guides/new-feature.md](guides/new-feature.md)

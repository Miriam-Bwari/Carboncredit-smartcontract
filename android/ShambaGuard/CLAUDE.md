# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (ProGuard + resource shrinking)
./gradlew test                   # Run unit tests
./gradlew testDebugUnitTest      # Run debug unit tests only
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew lint                   # Android lint
```

## Local Configuration

API keys and credentials are managed through `keys.properties` (gitignored). The Gradle build reads `keys.properties` and injects values into `BuildConfig` at compile time.

Required entries in `keys.properties`:
```properties
MAPS_API_KEY=your_google_maps_api_key
```

Never commit `keys.properties`. Never hardcode secrets in source files.

## Architecture Guidelines

**1. Paradigm: Feature-Based MVVM**
This project uses a **Feature-Based MVVM** architecture (NOT a strict single-activity architecture), with **Jetpack Compose UI** and **Hilt (Dagger)** for dependency injection.
- You should organize code by feature directories (e.g. `ui/features/<feature>`) rather than broadly grouping all views or all viewmodels together across the app.
- Although it relies heavily on Compose, the architecture is designed to support expanding to multiple activities depending on feature requirements.

**2. Guiding Principle: KISS (Keep It Simple, Stupid)**
Always prioritize clear, straightforward, and readable solutions. Avoid premature abstractions and over-engineered patterns. Focus on solving the specific problem in the simplest and most robust way possible.

**3. Iterative MVVM Evolution**
The project's MVVM implementation is a work in progress and may not be fully complete or perfectly consistent everywhere in the app. When adding or modifying code, gently improve the MVVM structure, but avoid unnecessarily completely rewriting working code.

**4. Shared Components (DRY Principle)**
Before creating new UI elements like text fields, buttons, or dialogs, **always** check the `sharedComposables/` directory. You must use or extend existing reusable Compose components (e.g., `ShambaButton`, `ShambaTextField`, `ShambaSnackbar`) to maintain visual consistency and avoid code duplication. If a necessary component doesn't exist yet, you should create a new reusable one in `sharedComposables/` rather than hardcoding it into a specific feature.

### Navigation
`ShambaGuardNavGraph.kt` is the central hub — utilizing **Navigation 3 `NavDisplay`** with a manual `backStack` (`mutableStateListOf<Any>`) and `@Serializable` Kotlin objects acting as keys. Bottom navigation uses a custom `BottomNavBar` observing a `BottomTab` sealed class layout with dynamic tabs based on the user's role (Admin, Agent, Farmer).

### Project & Layer Structure

The project is organized by distinct layers at the root, with a strong emphasis on **feature-based** modularity within the UI level.

```text
app/src/main/java/dev/korryr/shambaguard/
├── core/          # Core utilities (connectivity, base classes, extensions)
├── data/          # Global data sources (Room DB, Retrofit instances)
├── di/            # Hilt Dagger modules for dependency injection
├── navigation/    # Navigation 3 NavGraph, route keys, and BottomNavBar
├── repositories/  # Global repositories bridging data sources and UI
├── sharedComposables/ # Reusable Compose UI components (Shamba*)
└── ui/
    ├── theme/     # App-wide typography, colors, shapes
    └── features/  # Feature-based MVVM modules (admin, agent, farmer, auth)
```

### Standard Feature Directory Structure
When building or refactoring features, aim for the standard recommended Feature-Based MVVM structure. This isolates the data, domain, and presentation concerns into clear layers within the feature:

```text
ui/features/<feature>/
├── data/          # Data layer: Models, DTOs, API interfaces, and Repository implementations
├── domain/        # Domain layer: Use cases and Repository interfaces (optional, for complex business logic)
├── presentation/  # Presentation layer: ViewModels, UI State, and UI Events
└── view/          # UI layer: Jetpack Compose screens and feature-specific components
```

*Note: As the project is actively evolving, you may see older structures in existing features. When writing new features or modifying existing ones, always favor this standard approach where possible, applying the KISS principle.*

### Key Feature Areas
- **Auth** (`ui/features/auth/`): Phone OTP for farmers/agents, email + MFA for admins.
- **Admin** (`ui/features/admin/`): Pool health, farm maps with NDVI overlays, agent management.
- **Agent** (`ui/features/agent/`): Farmer registration, farm polygon drawing, evidence photos, sync status.
- **Farmer** (`ui/features/farmer/`): Dashboard with NDVI trends, drought warnings, policy management, payout history.

### Shared Components
`sharedComposables/` contains reusable Compose components prefixed with `Shamba` (e.g., `ShambaButton`, `ShambaTextField`, `ShambaSnackbar`). Use these before creating new ones.

## Tech Stack

- **Kotlin** `2.3.21`, **Compose BOM** `2026.04.01`, **Material 3**
- **Hilt (Dagger)** via **KSP** for dependency injection
- **Room** via **KSP** for local offline-first storage
- **Retrofit** + **OkHttp** for REST API communication with the FastAPI backend
- **WorkManager** for background offline sync
- **Google Maps Compose SDK** for farm polygon mapping
- **CameraX** for GPS-embedded evidence photo capture
- **Timber** for logging — initialized in `ShambaGuardApplication`, **debug builds only**
- **Kotlinx Serialization** for JSON and Navigation 3 route key serialization
- `compileSdk 36`, `targetSdk 36`, `minSdk 26`, **Java 21**

## Coding Rules

### Code Quality & Safety

- **No hardcoded strings or values**: All user-facing strings must go in `res/values/strings.xml`. All secrets and API keys go in `keys.properties` and are accessed via `BuildConfig`.
- **No `Log.d/e` calls**: Always use `Timber` for logging (e.g., `Timber.d(...)`, `Timber.e(...)`). `Timber` is already initialized in `ShambaGuardApplication`.
- **No `!!` (not-null assertions)**: Handle nullability explicitly using `?.`, `?:`, `requireNotNull()`, or safe `let` blocks. Force-unwrapping is forbidden.

### Architecture Rules

- **ViewModels must not hold Android `Context`**: If context is truly needed, inject `@ApplicationContext` via Hilt. Never pass `Activity` or `Fragment` context into a ViewModel.
- **No business logic in Composables**: Composable screens are purely for rendering UI and forwarding user events to the ViewModel. All logic belongs in the ViewModel or lower layers.
- **Prefer `StateFlow` / `MutableStateFlow` over `LiveData`**: Use `StateFlow` for all new UI state in ViewModels. `LiveData` should not be used in new code.
- **Repositories are the single source of truth**: ViewModels must never directly call Retrofit, Room, or any other data source. They must always go through a Repository.
- **Correct hiltViewModel Import**: Always use `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`. Do NOT use `import androidx.hilt.navigation.compose.hiltViewModel` as it is deprecated or incorrect for our current navigation setup.

### UI Rules

- **Always use Material 3 components**: Use `androidx.compose.material3` components. Do not use Material 2 (`androidx.compose.material`) or legacy View-based components in Compose screens.
- **Always handle loading, error, and empty states**: Every screen that fetches or displays data must explicitly handle and display all three states. Do not leave any of these as unhandled cases.
- **Use `collectAsStateWithLifecycle()`**: When collecting `StateFlow` in Composables, use `collectAsStateWithLifecycle()` (from `androidx.lifecycle:lifecycle-runtime-compose`) instead of `collectAsState()` for proper lifecycle awareness.

### Security Rules

- **Never commit secrets**: API keys, M-Pesa credentials, and JWT secrets must only ever live in `keys.properties`, which is gitignored. They are injected at compile time as `BuildConfig` fields. Never hardcode them in source files.
- **Timber logs are debug-only**: `Timber.DebugTree()` is only planted in debug builds. Never add custom release logging trees that emit sensitive data.

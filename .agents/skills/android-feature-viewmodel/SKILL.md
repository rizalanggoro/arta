---
name: android-feature-viewmodel
description: Defines how to create Android feature ViewModels with an in-class Factory companion object and a dedicated UiState backed by MutableStateFlow.
---

## Purpose

This skill defines the standard for Android ViewModels in this project.

Use this skill when you want a feature ViewModel to own its own factory, expose state through `MutableStateFlow`, and keep UI state in a separate `UiState` file.

---

## Core Rule

Each feature ViewModel MUST be defined in the feature presentation layer and MUST own its own factory inside the ViewModel file.

Rules:

- place the ViewModel in `feature/<feature>/presentation/<subfeature>`
- place the matching `UiState` in the same subfeature folder
- define the factory inside the ViewModel class as a `companion object`
- use `viewModelFactory { initializer { ... } }` for dependency creation
- do not create a separate factory file unless there is a strong project-wide reason

---

## Recommended Feature Shape

A feature subfeature should usually be organized like this:

```text
feature/
  auth/
    presentation/
      login/
        LoginScreen.kt
        LoginVM.kt
        LoginUiState.kt
      register/
        RegisterScreen.kt
        RegisterVM.kt
        RegisterUiState.kt
```

---

## ViewModel Rule

The ViewModel file MUST hold the state holder and business logic for the subfeature.

Rules:

- keep Compose UI code out of the ViewModel
- inject repositories, use cases, or other dependencies through the ViewModel factory
- update state through `MutableStateFlow`
- expose state as `StateFlow`
- use `viewModelScope` for async work
- keep event handling inside the ViewModel when needed

Recommended state pattern:

```kotlin
private val _uiState = MutableStateFlow(AuthUiState())
val uiState = _uiState.asStateFlow()
```

Recommended update pattern:

```kotlin
_uiState.update { it.copy(isLoading = true) }
```

---

## Factory Rule

The factory MUST live inside the ViewModel file.

Rules:

- define it in `companion object`
- expose it as `val Factory`
- use `viewModelFactory { initializer { ... } }`
- resolve dependencies from the `APPLICATION_KEY` or other supported creation extras when required
- keep the factory close to the ViewModel it creates

Example pattern:

```kotlin
companion object {
    val Factory = viewModelFactory {
        initializer {
            val authRepository = (this[APPLICATION_KEY] as MyApplication).authRepository
            AuthVM(
                authRepository = authRepository,
            )
        }
    }
}
```

Rules:

- do not move this factory into a separate file by default
- do not make screens construct repositories directly
- do not bypass the ViewModel factory with ad-hoc object creation in UI code

---

## UiState Rule

The UiState file MUST hold the state model for the subfeature.

Rules:

- use a `data class` for screen state unless a sealed state is more appropriate
- keep UI state free of Compose logic
- keep derived UI values in the ViewModel or compute them from state
- include loading, validation, and error fields when needed

Typical fields:

- input values
- loading flags
- validation errors
- submission flags
- view-specific messages

---

## State Ownership Rule

The ViewModel MUST own the mutable state.

Rules:

- use `MutableStateFlow` internally
- expose `StateFlow` publicly
- avoid mutable public state
- do not store screen state in the Composable when the ViewModel should own it

Required pattern:

```kotlin
private val _uiState = MutableStateFlow(AuthUiState())
val uiState = _uiState.asStateFlow()
```

---

## Event Rule

If the screen needs one-off events, the ViewModel SHOULD expose them separately from `uiState`.

Examples:

- snackbars
- navigation triggers
- toast messages
- validation result messages

Rules:

- use `SharedFlow`, `Channel`, or a similar event channel pattern
- do not overload `UiState` with transient events unless the project explicitly wants that
- keep event emission inside the ViewModel

---

## Dependency Rule

Feature ViewModels SHOULD receive dependencies through their factory.

Rules:

- repositories should be injected, not created inside the ViewModel body
- application-wide singletons may be resolved from `MyApplication` in the factory
- feature-specific dependencies should be created in the feature or app composition root and passed to the ViewModel factory
- avoid direct Retrofit or database access in the ViewModel

---

## Decision Rules

When deciding where code belongs, use these questions:

1. Is this the screen state model?
   - Put it in `UiState.kt`
2. Is this the state holder and business logic?
   - Put it in `VM.kt`
3. Is this a dependency wiring concern for the ViewModel?
   - Put it in the ViewModel companion object factory
4. Is this Compose UI?
   - Put it in `Screen.kt`
5. Is this a repository or data access concern?
   - Put it in `feature/<feature>/data`

---

## Quality Criteria

A valid feature ViewModel should satisfy all of these:

- the ViewModel owns its own factory in the same file
- the factory is exposed through a companion object `Factory`
- state is stored in `MutableStateFlow`
- state is exposed as `StateFlow`
- UI state lives in a separate `UiState` file
- the ViewModel contains business logic, not Compose UI
- dependencies are injected through the factory, not manually created in the screen

---

## Completion Check

Before considering the ViewModel complete, verify:

- the ViewModel file lives in `feature/<feature>/presentation/<subfeature>`
- the same subfeature has a dedicated `UiState.kt`
- the ViewModel contains a `companion object` factory
- the factory uses `viewModelFactory { initializer { ... } }`
- the ViewModel uses `MutableStateFlow` and `asStateFlow()`
- the screen does not create dependencies that belong in the ViewModel
- the file naming clearly matches the feature and subfeature

---

## Example Prompts

Use this skill when you want to:

- create a feature ViewModel with an internal factory
- standardize ViewModel state handling in Android
- keep UiState and ViewModel logic separated
- wire application dependencies into ViewModels cleanly

Example prompts:

- "Buat ViewModel auth dengan factory di companion object"
- "Susun ViewModel feature ini supaya pakai MutableStateFlow dan UiState terpisah"
- "Tambahkan Factory di dalam ViewModel login"
- "Buat pola ViewModel yang konsisten untuk semua feature Android"

---

## Notes

This skill is intentionally strict.

If a feature ViewModel exists, it should own its factory.

If a screen only needs a simple state holder, still prefer the same pattern for consistency.

If the project later grows, you can extend this skill with more detailed rules for:

- saved state handling
- navigation side effects
- assisted injection patterns
- testing ViewModels and factories

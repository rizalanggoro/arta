---
name: android-feature-screen
description: Defines how to create Android feature screen files with a Screen composable, a private Content composable for state hoisting, and previews for each screen scenario.
---

## Purpose

This skill defines how to write a feature screen file inside an Android `feature` subfeature.

Use this skill when creating a screen such as login, register, create, update, detail, or other feature-specific UI.

---

## Core Rule

Every feature screen file MUST use Jetpack Compose.

The screen file MUST be focused on UI and state hoisting only.

---

## Required Structure

Each screen file MUST contain at least these two main composables:

1. the public feature-specific screen composable, such as `AuthScreen`, `LoginScreen`, `RegisterScreen`, `CreateCategoryScreen`, or `DetailTodoScreen`
2. the private `Content` composable

Rules:

- the public composable name MUST include the feature or subfeature name
- examples: `AuthScreen`, `LoginScreen`, `RegisterScreen`, `CreateCategoryScreen`, `DetailTodoScreen`
- `Screen` is the entry composable for the feature screen
- `Content` is used for state hoisting and UI rendering
- `Screen` should be the composable that gets registered in `ComposeApp.kt`
- `Content` should receive the screen state and callbacks as parameters
- do not collapse all UI into a single function if the screen is expected to support previews and state hoisting

---

## Screen Rule

The public feature-specific screen composable MUST be responsible for:

- collecting state from the ViewModel
- creating screen-scoped UI helpers such as `SnackbarHostState` when needed
- handling side effects such as event collection
- passing state and callbacks into `Content`

Rules:

- keep business logic out of the screen
- keep UI state collection in the screen entry point
- `LocalBackStack` MUST only be initialized or read inside `Screen`
- `Content` MUST NOT access `LocalBackStack` directly
- do not place layout-heavy code directly inside `Screen` if it can be moved into `Content`

---

## Content Rule

The `Content` composable MUST be responsible for:

- rendering the actual UI
- receiving state through parameters
- receiving callbacks through parameters
- supporting previews

Rules:

- use parameter-based state hoisting
- keep `Content` private unless the project explicitly needs otherwise
- keep the UI declarative and reusable
- keep navigation dependencies such as `LocalBackStack` out of `Content`
- do not call the ViewModel from `Content`

---

## Preview Rule

The `Content` composable MUST have previews for each important scenario.

Common scenarios:

- default or normal state
- loading state
- alternate mode state such as login/register
- error or empty state when applicable

Rules:

- every meaningful screen variant should have a preview
- preview functions should be private when possible
- keep previews close to the Content composable they represent
- use the app theme in previews

---

## Example Pattern

A valid screen file may look like this:

```kotlin
@Composable
fun AuthScreen(vm: AuthVM = viewModel(factory = AuthVM.Factory)) {
    val uiState by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.messageEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        isLogin = uiState.isLogin,
        isLoading = uiState.isLoading,
        name = uiState.name,
        email = uiState.email,
        password = uiState.password,
        emailError = uiState.emailError,
        passwordError = uiState.passwordError,
        nameError = uiState.nameError,
        confirmPasswordError = uiState.confirmPasswordError,
        confirmPassword = uiState.confirmPassword,
        onChangeName = vm::onChangeName,
        onChangeEmail = vm::onChangeEmail,
        onChangePassword = vm::onChangePassword,
        onChangeConfirmPassword = vm::onChangeConfirmPassword,
        onClickSwitch = vm::onChangeMode,
        onClickSubmit = {
            when (uiState.isLogin) {
                true -> vm.login()
                else -> vm.register()
            }
        },
    )
}

@Composable
private fun Content(
    snackbarHostState: SnackbarHostState,
    isLogin: Boolean = true,
    isLoading: Boolean = false,
    name: String = "",
    email: String = "",
    password: String = "",
    confirmPassword: String = "",
    emailError: String? = null,
    passwordError: String? = null,
    nameError: String? = null,
    confirmPasswordError: String? = null,
    onChangeName: (String) -> Unit = {},
    onChangeEmail: (String) -> Unit = {},
    onChangePassword: (String) -> Unit = {},
    onChangeConfirmPassword: (String) -> Unit = {},
    onClickSwitch: () -> Unit = {},
    onClickSubmit: () -> Unit = {}
) {
    // UI goes here
}
```

---

## Screen Responsibilities by Layer

### Screen

The `Screen` composable should:

- be the composable registered by navigation
- observe ViewModel state
- manage one-off side effects
- pass data into `Content`

### Content

The `Content` composable should:

- define the visual layout
- render widgets and controls
- expose event callbacks through parameters
- support preview variants

---

## Decision Rules

When deciding where code belongs, use these questions:

1. Is this the composable entry point for a feature screen?
   - Put it in a feature-specific screen composable such as `AuthScreen` or `LoginScreen`
2. Is this the UI layout and state-hoisted rendering?
   - Put it in `Content`
3. Is this one-off event collection or screen-scoped helper state?
   - Put it in `Screen`
4. Is this previewable UI?
   - Put it in `Content`
5. Is this business logic or state management?
   - Put it in the ViewModel, not in the screen file

---

## Quality Criteria

A valid feature screen should satisfy all of these:

- uses Jetpack Compose
- has a public feature-specific screen composable as the feature entry point
- has a private `Content` composable for state hoisting
- supports previews for the main screen scenarios
- keeps logic out of the UI rendering function
- keeps the file easy to register in `ComposeApp.kt`

---

## Completion Check

Before considering a screen complete, verify:

- the file contains a public feature-specific screen composable such as `AuthScreen`, `LoginScreen`, or `RegisterScreen`
- the file contains a private `Content` composable
- the screen composable collects ViewModel state
- `Content` receives state and callbacks via parameters
- previews exist for the important screen states
- the screen can be registered in Compose navigation

---

## Example Prompts

Use this skill when you want to:

- create a login screen for Android Compose
- create a register screen with previews
- refactor a screen to separate Screen and Content
- enforce consistent screen file structure in a feature subfolder

Example prompts:

- "Buat AuthScreen dengan Content dan preview untuk login/register"
- "Susun screen feature create supaya ada Screen dan Content"
- "Refactor file screen ini supaya state hoisting di Content"
- "Buat preview untuk loading dan default state"

---

## Notes

This skill is intentionally strict.

If a file is a screen file, it should have a clear `Screen` composable and a private `Content` composable.

`LocalBackStack` is a screen-scoped dependency and must never be initialized or consumed inside `Content`.

If the screen has multiple scenarios, provide previews for each important scenario.

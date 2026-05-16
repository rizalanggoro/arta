---
name: android-compose-navigation3
description: Defines an Android project standard that requires Jetpack Compose for UI and Jetpack Compose Navigation 3 for app navigation. Use when creating or refactoring Android apps to enforce Compose-only screens and navigation.
---

## Purpose

This skill defines the Android UI and navigation standard for the project.

The app MUST use:

- Jetpack Compose for all UI
- Jetpack Compose Navigation 3 for app navigation

Use this skill when creating a new Android app or refactoring an existing app so the UI stack stays modern, consistent, and Compose-first.

This skill also defines the required Navigation 3 route registry and app entry wiring.

---

## UI Rule

All user-facing UI MUST be built with Jetpack Compose.

This means:

- use `@Composable` functions for screens and components
- build screens with Compose layouts and state
- keep UI logic in composables or feature state holders
- avoid XML layout files for app screens

Rules:

- do not introduce `Activity`-based XML UI for new screens
- do not mix XML views with Compose for normal screen development
- keep Compose as the primary UI layer across the project

---

## Navigation Rule

All in-app navigation MUST use Jetpack Compose Navigation 3.

Use it for:

- screen routing
- back stack handling
- nested flows
- feature transitions

Rules:

- define navigation in Compose
- keep navigation paths and destinations in a dedicated navigation layer
- do not use legacy fragment navigation for new flows
- do not switch back to XML navigation patterns

Navigation 3 route definitions MUST live in `core/Routes.kt`.

Navigation 3 back stack composition MUST use `LocalBackStack` from `core/Compositions.kt`.

The main app entry composable MUST live in `core/ComposeApp.kt`.

The app should follow the route-driven flow:

1. define routes in `Routes.kt`
2. expose the back stack in `Compositions.kt`
3. create the navigation host in `ComposeApp.kt`
4. branch between auth and app flows from the session state

`ComposeApp.kt` MUST NOT define user-facing screen composables.

All screens must live in `feature/<feature>/presentation/<subfeature>` files.

`ComposeApp.kt` may only contain app shell composables, navigation host composables, and route-branch helpers that do not render standalone screens.

---

## Recommended Structure

A Compose-first Android app should usually keep these responsibilities separate:

- `core` for shared app setup, theme, navigation host, and reusable UI utilities
- `feature` for feature screens and feature state
- `domain` for business models and contracts

Typical Compose-related folders:

- `core/compose`
- `core/routes`
- `core/theme`
- `core/compositions`
- `feature/<feature>/screen`
- `feature/<feature>/component`
- `feature/<feature>/navigation`

Required core files for Navigation 3:

- `core/Routes.kt`
- `core/ComposeApp.kt`
- `core/Compositions.kt`

---

## Screen Rules

Each screen SHOULD be represented as a composable.

Rules:

- keep screens small and focused
- split reusable UI into composable components
- hoist state when needed
- avoid putting unrelated logic directly inside the UI tree
- define every user-facing screen in `feature/<feature>/presentation/<subfeature>`
- do not define screen composables in `core/ComposeApp.kt`
- do not place feature screen UI in `core` files

Good patterns:

- `Screen` composable for the page
- smaller composables for sections and repeated UI
- `State` and `Event` types for feature behavior

Feature composables that participate in navigation SHOULD read the back stack from `LocalBackStack` instead of creating a separate back stack in every screen.

---

## Navigation 3 Rules

When using Navigation Compose 3, follow these guidelines:

- use a single navigation host entry point
- define destinations as composable targets
- keep route definitions explicit and readable
- keep feature navigation logic close to the feature when practical
- centralize app-level routes in the core navigation layer

If a feature owns multiple screens, keep its route definitions grouped together.

### Route Registry Rules

The `Routes.kt` file MUST define the canonical route list for the app.

Rules:

- use a single `Routes` object
- mark `Routes` and each route type with `@Serializable`
- each route type MUST implement `NavKey`
- route types with no arguments MUST use `data object`
- route types with arguments MUST use `data class`
- do not define route strings scattered across feature files
- do not use raw string routes when a `NavKey` route exists

Example pattern:

```kotlin
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
   @Serializable
   data object AuthRoute : NavKey

   @Serializable
   data object ListTodoRoute : NavKey

   @Serializable
   data class TodoFormRoute(
      val todoId: Int? = null
   ) : NavKey
}
```

### Back Stack Composition Rules

The `Compositions.kt` file MUST expose the shared navigation back stack.

Required pattern:

```kotlin
val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
   error("error: LocalBackStack not provided")
}
```

Rules:

- provide the back stack once at the appropriate navigation boundary
- share it with child composables through `CompositionLocalProvider`
- do not create separate local back stack implementations in multiple files

### Compose App Entry Rules

The `ComposeApp.kt` file MUST act as the app-level navigation entry point.

Required behavior:

- read application-level dependencies from the `Application` class when needed
- read session or auth state to decide which navigation branch to show
- wrap the app content in `TodoListTheme(darkTheme = isDark)`
- wrap the themed content in `Surface`
- create a back stack with `rememberNavBackStack(...)`
- provide the back stack with `CompositionLocalProvider(LocalBackStack provides backStack)`
- render navigation with `NavDisplay`
- attach `rememberSaveableStateHolderNavEntryDecorator()` and `rememberViewModelStoreNavEntryDecorator()` as entry decorators
- use one branch for auth flow and one branch for authenticated flow when needed
- keep all screen composables outside `ComposeApp.kt`

Example pattern:

```kotlin
@Composable
fun ComposeApp(themeRepository: ThemeRepository) {
   val authRepository =
      (androidx.compose.ui.platform.LocalContext.current.applicationContext as MyApplication).authRepository

   val isDark by themeRepository.isDark.collectAsState()
   val session by authRepository.session.collectAsState()

   TodoListTheme(darkTheme = isDark) {
      Surface {
         when (session) {
            null -> AuthNavDisplay()
            else -> TodoNavDisplay()
         }
      }
   }
}
```

In this pattern, `AuthNavDisplay()` and `TodoNavDisplay()` are navigation hosts, not screen definitions.

Screen composables such as `LoginScreen`, `RegisterScreen`, `ListTodoScreen`, or `DetailTodoScreen` MUST remain in feature presentation files.

The auth branch SHOULD start with:

```kotlin
val backStack = rememberNavBackStack(Routes.AuthRoute)
```

The authenticated branch SHOULD use the main home route, for example `Routes.ListTodoRoute`.

### NavDisplay Rules

The navigation host inside `ComposeApp.kt` MUST use `NavDisplay`.

Required pattern:

```kotlin
CompositionLocalProvider(LocalBackStack provides backStack) {
   NavDisplay(
      backStack = backStack,
      entryDecorators = listOf(
         rememberSaveableStateHolderNavEntryDecorator(),
         rememberViewModelStoreNavEntryDecorator()
      ),
      entryProvider = entryProvider {
         entry<Routes.AuthRoute> { AuthScreen() }
      }
   )
}
```

Rules:

- always provide the back stack through `CompositionLocalProvider`
- always pass the same back stack into `NavDisplay`
- always include saveable state and view model store decorators unless there is a strong reason not to
- define entries with `entryProvider { ... }`
- keep auth and authenticated flow hosts as Compose navigation trees, not XML or fragment hosts

If the auth flow and authenticated flow are split into separate composable functions, use this pattern:

```kotlin
@Composable
private fun AuthNavDisplay() {
   val backStack = rememberNavBackStack(Routes.AuthRoute)

   CompositionLocalProvider(LocalBackStack provides backStack) {
      NavDisplay(
         backStack = backStack,
         entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
         ),
         entryProvider = entryProvider {
            entry<Routes.AuthRoute> { AuthScreen() }
         }
      )
   }
}

@Composable
private fun TodoNavDisplay() {
   val backStack = rememberNavBackStack(Routes.ListTodoRoute)

   CompositionLocalProvider(LocalBackStack provides backStack) {
      NavDisplay(
         backStack = backStack,
         entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
         ),
         entryProvider = entryProvider {
            entry<Routes.ListTodoRoute> { ListTodoScreen() }
            entry<Routes.TodoFormRoute> { CreateTodoScreen(todoId = it.todoId) }
            entry<Routes.DetailTodoRoute> { DetailTodoScreen(id = it.id) }
            entry<Routes.CategoryRoute> { CategoryScreen() }
         }
      )
   }
}
```

Rules for split navigation hosts:

- each branch MUST create its own `backStack` with the correct root route
- each branch MUST provide `LocalBackStack` before rendering `NavDisplay`
- each branch MUST use the same decorator list pattern
- auth flow MUST start from `Routes.AuthRoute`
- authenticated flow MUST start from the main application route, such as `Routes.ListTodoRoute`
- feature destinations MAY be grouped inside the authenticated branch when they belong to the same app flow
- branch helpers in `ComposeApp.kt` MUST only wire routes to feature screen composables; they MUST NOT implement the screen UI themselves

---

## Decision Rules

When deciding how to implement UI or navigation, use these questions:

1. Is this a screen or visual component?
   - Make it a composable in `feature/<feature>/presentation/<subfeature>`
2. Is this app-wide routing or host setup?
   - Put it in `core`
3. Is this feature-specific navigation or screen flow?
   - Put it in the feature folder
4. Is this reusable UI across multiple screens?
   - Put it in shared Compose components
5. Is this legacy XML or fragment-based screen code?
   - Do not use it for new Compose-first work
6. Is this the canonical route list or app entry wiring?
   - Put it in `core/Routes.kt`, `core/Compositions.kt`, or `core/ComposeApp.kt`

---

## Quality Criteria

A valid Compose-first Android project should satisfy all of these:

- all new UI screens are built with Compose
- Navigation Compose 3 is used for app routing
- navigation host setup is centralized and readable
- routes are defined in one canonical `Routes.kt` file
- the shared back stack is provided through `LocalBackStack`
- the app entry composable owns auth-vs-app flow selection
- no user-facing screen composable is defined in `core/ComposeApp.kt`
- every screen composable lives in `feature/<feature>/presentation/<subfeature>`
- reusable UI pieces are extracted into components
- feature UI stays isolated inside feature folders
- the project does not drift back to XML screen development

---

## Completion Check

Before considering the work complete, verify:

- no new screen was implemented with XML layouts
- navigation uses Compose Navigation 3
- route keys are defined as `@Serializable` `NavKey` types in `core/Routes.kt`
- `LocalBackStack` is provided from `core/Compositions.kt`
- the app entry point lives in `core/ComposeApp.kt`
- `ComposeApp.kt` does not define user-facing screen composables
- every screen composable lives under `feature/<feature>/presentation/<subfeature>`
- screen state is handled cleanly in Compose
- shared app routes and navigation host are organized in the correct layer
- the UI stack stays consistent across features

---

## Example Prompts

Use this skill when you want to:

- create a Compose-only Android app
- refactor a screen from XML to Compose
- set up Navigation Compose 3 for the app
- define a Compose navigation structure for multiple features

Example prompts:

- "Buat Android project yang UI-nya pakai Jetpack Compose dan navigasi pakai Navigation 3"
- "Refactor screen ini supaya jadi Compose"
- "Susun navigation host Compose untuk app ini"
- "Tambahkan pattern Compose-first untuk semua feature"
- "Buat Routes.kt di core untuk Navigation 3 dengan NavKey dan @Serializable"
- "Buat ComposeApp.kt yang pakai rememberNavBackStack dan LocalBackStack"

---

## Notes

This skill is intentionally strict.

If a new screen is requested, the default implementation should be Compose.

If navigation is required, use Navigation Compose 3 rather than legacy navigation approaches.

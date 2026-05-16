---
name: android-manual-dependency-injection
description: Defines a manual dependency injection pattern for Android apps where Application acts as the composition root and initializes repositories, services, and factories for ViewModels and features.
---

## Purpose

This skill defines how to build Android dependency wiring without a DI framework.

The `Application` class acts as the composition root and initializes app dependencies that will be reused by:

- ViewModels
- repositories
- feature screens
- other app-level services

Use this skill when you want a lightweight manual DI approach and do not want Hilt, Koin, or another DI framework.

---

## Core Idea

The `Application` class is responsible for creating long-lived dependencies once at app startup.

Typical examples:

- repositories
- local data sources
- remote data sources
- shared managers
- feature factories
- app-wide services

The app then passes those dependencies down explicitly.

---

## Recommended Location

Keep the application entry point inside the Android core layer, for example:

```text
app/src/main/java/<package>/core/application/
```

Common structure:

```text
core/
  application/
    MyApplication.kt
  di/
    AppContainer.kt
    ViewModelFactory.kt
```

If the project already uses a different folder style, keep the same package root and place the composition root inside `core`.

---

## Rules

### 1. Application Owns Initialization

`Application` MUST initialize shared dependencies in one place.

Example responsibilities:

- create repositories
- create services
- create factories
- wire dependencies in the correct order

The application should not contain UI logic.

---

### 2. Manual Wiring Only

Dependencies MUST be created explicitly with constructors or simple factories.

Rules:

- do not use automatic DI code generation
- do not rely on reflection-based injection
- do not hide dependency creation behind complex magic
- keep the object graph easy to read

---

### 3. Dependency Order Must Be Explicit

If one repository depends on another, create the dependency first.

Example order:

1. application context
2. low-level data sources
3. repositories
4. use cases or coordinators
5. ViewModel factories

---

### 4. ViewModels Receive Dependencies Manually

ViewModels MUST receive dependencies through constructors or a factory.

Preferred pattern:

- `Application` creates repositories
- a `ViewModelFactory` passes dependencies into the ViewModel
- the feature screen obtains the ViewModel from that factory

Do not instantiate repositories directly inside ViewModels.

---

### 5. Repositories Can Depend on Application Context

If a repository needs Android context, it may receive `Application` or `Context` from the composition root.

Rules:

- use `Application` or `applicationContext` for app-wide needs
- do not store short-lived activity context in long-lived objects
- avoid leaking UI context into repositories

---

### 6. Keep the Graph Simple

The dependency graph should stay readable.

Good signs:

- the app is easy to reason about
- dependency creation is visible in one place
- objects are passed explicitly
- initialization order is obvious

Bad signs:

- hidden global singletons everywhere
- duplicated setup in multiple screens
- ViewModels constructing repositories by themselves
- circular dependency chains

---

## Example Pattern

A manual DI application may look like this conceptually:

```kotlin
class MyApplication : Application() {
    lateinit var themeRepository: ThemeRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var todoRepository: TodoRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    override fun onCreate() {
        super.onCreate()

        themeRepository = ThemeRepository(this)
        authRepository = AuthRepository(this)
        todoRepository = TodoRepository(this)
        categoryRepository = CategoryRepository(
            context = this,
            todoRepository = todoRepository,
        )
    }
}
```

This pattern is valid when:

- the repositories are app-wide and long-lived
- initialization order is explicit
- dependencies are exposed in a controlled way

---

## ViewModel Access Pattern

A feature should receive dependencies from the application graph, not create them internally.

Typical options:

- a custom `ViewModelFactory`
- a feature-specific factory object
- explicit constructor injection through a screen-level setup function

Rules:

- keep ViewModel creation outside the ViewModel class
- pass only what the ViewModel needs
- avoid building the whole graph inside the feature screen

---

## Decision Rules

When deciding where a dependency belongs, use these questions:

1. Is this long-lived and shared by the app?
   - initialize it in `Application`
2. Is this a data access object or repository?
   - create it from the composition root
3. Is this only needed by one feature screen?
   - pass it into that feature explicitly
4. Does this object need Android app context?
   - give it `Application` or `applicationContext`, not an Activity
5. Does this dependency need complex lifecycle handling?
   - keep the lifecycle in the composition root, not the feature

---

## Quality Criteria

A valid manual DI setup should satisfy all of these:

- `Application` is the single composition root
- dependencies are created explicitly
- ViewModels do not create repositories themselves
- app context is used safely
- object creation order is readable
- the graph stays simple enough to maintain without a DI framework

---

## Completion Check

Before considering the setup complete, verify:

- all shared dependencies are initialized in `Application` or a simple root container
- no ViewModel creates app repositories directly
- no feature hides dependency creation inside screen logic
- context usage is safe and long-lived objects do not leak Activity references
- dependency wiring can be traced by reading the code top to bottom

---

## Example Prompts

Use this skill when you want to:

- create a manual DI `Application` class for Android
- refactor existing Android code into explicit constructor injection
- wire repositories into ViewModels without Hilt
- set up a simple app container in `core`

Example prompts:

- "Buat manual dependency injection pakai Application di Android"
- "Susun repository dan ViewModel tanpa Hilt"
- "Refactor application supaya jadi composition root"
- "Tambahkan AppContainer untuk wiring dependency"

---

## Notes

This skill is intentionally framework-free.

Use it when you want a small, explicit, and easy-to-debug dependency graph.

If the app later grows large, you can extend this skill with more detailed rules for:

- ViewModel factories
- app container objects
- module split per feature
- test doubles and fake repositories
- lifecycle-safe resource cleanup

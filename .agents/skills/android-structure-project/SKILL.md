---
name: android-structure-project
description: Defines an Android app architecture with three main folders: core, domain, and feature. Use when creating or refactoring Android apps to keep utilities, business models, and features cleanly separated.
---

## Purpose

This skill defines a reusable Android project structure that keeps the app organized into three main top-level folders:

1. `core`
2. `domain`
3. `feature`

Use this skill when starting a new Android app or restructuring an existing one so the codebase stays modular, testable, and easy to grow.

---

## Project Shape

The Android app SHOULD be organized like this:

- `core` for shared app infrastructure and app-wide utilities
- `domain` for business entities and use-case level models
- `feature` for feature-based screens and flows

Recommended root layout:

```text
app/
  src/main/java/<package>/
    core/
    domain/
    feature/
```

If the project already has a different package structure, keep the same package root and apply the same separation inside it.

---

## Core Folder

`core` contains shared implementation details that are not business-specific.

Typical contents:

- utility helpers
- app initialization code
- Compose app setup
- navigation routes
- shared theme and design system
- common base classes or shared UI components
- networking and persistence setup if they are app-wide

Rules:

- Keep reusable technical code here
- Do not put business entities here
- Do not put feature screen logic here
- If code is used by multiple features and is not domain-specific, it belongs here

Suggested subfolders:

- `core/application`
- `core/compose`
- `core/routes`
- `core/util`
- `core/theme`
- `core/network`
- `core/storage`

---

## Domain Folder

`domain` contains business-level models and contracts.

Use `domain` for:

- `Todo`
- `Category`
- `User`
- `Session`
- other business objects that also exist in the backend domain

Rules:

- Keep the same meaning as backend domain entities when possible
- Prefer plain Kotlin data classes or sealed types for business data
- Avoid Android framework dependencies in this layer
- Avoid Compose UI code in this layer
- Avoid Retrofit, Room, or other infrastructure details in this layer

Suggested subfolders:

- `domain/model`
- `domain/repository`
- `domain/usecase`
- `domain/entity`

If the backend already has a domain concept, mirror it here with the same name and similar field shape unless Android-specific constraints require a small adjustment.

---

## Feature Folder

`feature` contains user-facing application flows, one folder per feature.

Examples:

- `feature/auth`
- `feature/category`
- `feature/todo`
- `feature/user`

Each feature SHOULD own its own screen logic, view model, state, and feature-specific UI.

Recommended feature structure:

```text
feature/auth/
  AuthScreen.kt
  AuthViewModel.kt
  AuthState.kt
  AuthEvent.kt
  components/
```

Rules:

- Keep feature-specific UI inside the feature folder
- Keep feature-specific state and event handling inside the feature folder
- A feature may depend on `domain` and `core`
- A feature should not directly own shared app-wide utilities
- If something is reused by multiple features, move it to `core`

---

## Dependency Direction

Use one-way dependencies:

- `feature` may depend on `core` and `domain`
- `domain` should not depend on `feature`
- `core` should not depend on `feature`
- `domain` should avoid Android framework dependencies

Preferred direction:

`feature` -> `domain` -> nothing
`feature` -> `core` -> nothing

If a dependency points backward into a feature, extract the shared code first.

---

## Decision Rules

When deciding where code belongs, use these questions:

1. Is this reusable app infrastructure?
   - Put it in `core`
2. Is this a business object or business contract?
   - Put it in `domain`
3. Is this tied to one user flow or screen group?
   - Put it in `feature`
4. Is it shared by multiple features but not business logic?
   - Put it in `core`
5. Does it mirror backend data and app-wide business meaning?
   - Put it in `domain`

---

## Quality Criteria

A valid structure should satisfy all of these:

- `core`, `domain`, and `feature` are the top-level organization
- business entities are centralized in `domain`
- screens and flows are isolated by feature
- shared app concerns live in `core`
- cross-feature reuse does not leak feature-specific code into shared layers
- the codebase stays easy to navigate as new features are added

---

## Completion Check

Before considering the structure complete, verify:

- every new screen belongs to exactly one feature folder
- every business entity has a clear place in `domain`
- reusable app helpers are not duplicated across features
- shared navigation or app setup stays in `core`
- folder names stay consistent across the project

---

## Example Prompt Usage

Use this skill when you want to:

- create a new Android app with `core`, `domain`, and `feature`
- refactor an existing app into a cleaner structure
- decide where a class, screen, or model should live
- keep Android code aligned with backend domain concepts

Example prompts:

- "Buat struktur Android project dengan core, domain, dan feature"
- "Refactor app ini supaya domain model dipisah dari feature"
- "Tentukan folder yang tepat untuk class ini"
- "Susun folder feature auth, category, dan todo"

---

## Notes

If the project later grows, extend this skill with more detailed rules for:

- dependency injection
- navigation architecture
- state management
- offline cache and sync
- design system and UI components

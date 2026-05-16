---
name: android-domain
description: Defines how Android domain models should mirror backend/API domain models using @Serializable data classes.
---

## Purpose

This skill defines how to create Android domain models that represent the same business concepts as the backend/API domain.

Use this skill when you want the Android app to keep its domain layer aligned with the server contract and business meaning.

---

## Core Rule

Android domain models MUST represent the backend/API domain as closely as possible.

That means:

- if the backend has `Category`, Android should also have `Category`
- if the backend has `User`, Android should also have `User`
- if the backend has `Session`, Android should also have `Session`
- field names and meanings should stay consistent unless Android needs a small adjustment

The domain layer is not where UI state, Compose code, or infrastructure details belong.

---

## Model Shape Rule

Every Android domain model MUST use:

- `@Serializable`
- `data class`

Rules:

- do not use regular classes for domain entities
- do not use objects for domain entities
- do not define domain entities without `@Serializable`
- keep domain models as plain data holders

---

## Example Pattern

A valid Android domain model should look like this:

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val ownerEmail: String
)
```

This same rule applies to other domain entities such as:

- `User`
- `Session`
- `Todo`
- `Transaction`
- `Wallet`
- `Gold`
- `GoldPrice`

---

## Mapping Rule

When creating domain models, use the backend/API domain as the source of truth.

Rules:

- keep the same concept name when possible
- keep the same meaning even if the UI uses a different label
- if the backend changes, update the Android domain model to match
- if the backend includes nested business objects, mirror them in Android domain when needed

If an Android-specific adjustment is necessary, keep it minimal and document the reason in the code or prompt.

---

## Folder Rule

Android domain models SHOULD live under the `domain` folder.

Recommended location:

```text
app/src/main/java/<package>/domain/
```

Suggested structure:

```text
domain/
  Category.kt
  User.kt
  Session.kt
  Todo.kt
  Transaction.kt
  Wallet.kt
  Gold.kt
  GoldPrice.kt
```

Rules:

- keep domain entities separated from feature code
- do not place domain entities inside `core`
- do not place domain entities inside feature folders unless they are temporary and local to a refactor

---

## Dependency Rule

Domain models MUST stay independent from Android UI and infrastructure.

Rules:

- no Compose code
- no XML code
- no Retrofit annotations unless explicitly required by project convention
- no Room entities unless the project explicitly uses the same class for persistence
- no ViewModel logic
- no repository logic

The domain layer should remain reusable and easy to map from backend data.

---

## Decision Rules

When deciding whether a class belongs in Android domain, use these questions:

1. Is this a business concept that also exists in the backend?
   - Put it in `domain`
2. Is this just UI state or screen state?
   - Put it in `feature`
3. Is this app-level infrastructure or wiring?
   - Put it in `core`
4. Is this a pure data representation of a backend entity?
   - Put it in `domain`
5. Does this class need Compose or Android UI dependencies?
   - It does not belong in `domain`

---

## Quality Criteria

A valid Android domain layer should satisfy all of these:

- domain classes mirror backend/API business concepts
- each model is a `@Serializable data class`
- domain models stay free of UI and infrastructure concerns
- names and field meaning stay aligned with backend entities
- the domain layer stays simple enough to map between API and UI

---

## Completion Check

Before considering a domain model complete, verify:

- the entity name matches the backend concept
- the model uses `@Serializable`
- the model uses `data class`
- the fields represent the same business meaning as the backend
- the class is placed in the `domain` folder
- no UI or repository logic leaked into the model

---

## Example Prompts

Use this skill when you want to:

- create Android domain models from backend domain entities
- mirror API entities in the Android app
- define `@Serializable` models for shared business concepts
- keep Android domain aligned with backend naming and fields

Example prompts:

- "Buat domain Android yang sama seperti domain backend"
- "Buat model Category di Android dengan @Serializable data class"
- "Mirror domain User dari backend ke Android"
- "Susun domain layer Android untuk Todo, Session, dan Wallet"

---

## Notes

This skill is intentionally strict.

If a class is meant to represent a backend/API business entity, it should be a `@Serializable data class` in the Android domain layer.

If the class is about UI, state, wiring, or infrastructure, it does not belong here.

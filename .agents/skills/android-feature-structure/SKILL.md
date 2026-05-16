---
name: android-feature-structure
description: Defines how to create Android features inside the feature folder with data and presentation layers, and a required Screen, ViewModel, and UiState file for each presentation subfeature.
---

## Purpose

This skill defines how to structure an Android feature inside the `feature` folder.

Use this skill when you want each feature to be split into a `data` layer and a `presentation` layer, where presentation is organized into subfeatures such as `create`, `update`, `detail`, or `list`, with a consistent file set in every subfeature.

---

## Core Rule

Every Android feature MUST live inside the `feature` folder.

Examples:

- `feature/auth`
- `feature/category`
- `feature/todo`
- `feature/user`

Each feature MUST contain:

- `data`
- `presentation`

The `data` folder holds repository-related code and other data-layer concerns.

The `presentation` folder holds subfeatures.

Each presentation folder MAY contain multiple subfeatures.

Examples:

- `create`
- `update`
- `detail`
- `list`
- `delete`

---

## Subfeature Rule

Each presentation subfeature MUST contain exactly these three files:

1. `Screen`
2. `ViewModel`
3. `UiState`

Rules:

- the file names must reflect the feature and subfeature name
- the three files must live in the same presentation subfeature folder
- do not skip any of the three files
- do not combine all responsibilities into one file if the subfeature is expected to be complete

---

## Naming Rule

The subfeature name should be reflected in the file names.

Example for the `category` feature with a `create` subfeature:

- `CreateCategoryScreen.kt`
- `CreateCategoryVM.kt`
- `CreateCategoryUiState.kt`

Another example for the `todo` feature with a `detail` subfeature:

- `DetailTodoScreen.kt`
- `DetailTodoVM.kt`
- `DetailTodoUiState.kt`

Rules:

- use a clear feature-based prefix or suffix so the file is easy to identify
- keep naming consistent across all features
- avoid generic names that become ambiguous in larger apps

---

## Recommended Folder Shape

A feature should usually be organized like this:

```text
feature/
  category/
    data/
      CategoryRepository.kt
      CategoryRemoteDataSource.kt
      CategoryLocalDataSource.kt
    presentation/
      create/
        CreateCategoryScreen.kt
        CreateCategoryVM.kt
        CreateCategoryUiState.kt
      update/
        UpdateCategoryScreen.kt
        UpdateCategoryVM.kt
        UpdateCategoryUiState.kt
      detail/
        DetailCategoryScreen.kt
        DetailCategoryVM.kt
        DetailCategoryUiState.kt
```

Another example:

```text
feature/
  todo/
    data/
      TodoRepository.kt
      TodoRemoteDataSource.kt
    presentation/
      list/
        ListTodoScreen.kt
        ListTodoVM.kt
        ListTodoUiState.kt
      detail/
        DetailTodoScreen.kt
        DetailTodoVM.kt
        DetailTodoUiState.kt
```

---

## File Responsibility Rule

### Screen

The `Screen` file MUST hold the composable UI for the subfeature.

Rules:

- use Compose for the UI
- keep the screen focused on rendering and basic event forwarding
- do not put business logic inside the screen

### ViewModel

The `ViewModel` file MUST hold the state holder and logic for the subfeature.

Rules:

- manage UI state and events here
- call repositories, use cases, or other dependencies here
- do not place Compose UI code in the ViewModel

### UiState

The `UiState` file MUST hold the state model for the subfeature.

Rules:

- use a data class or sealed structure as appropriate
- represent screen state clearly
- keep it free of UI rendering code

---

## Feature Boundary Rule

A feature folder SHOULD stay focused on one business area.

Examples:

- `feature/category` should contain only category-related data and presentation flows
- `feature/auth` should contain only authentication-related data and presentation flows
- `feature/todo` should contain only todo-related data and presentation flows
- `feature/user` should contain only user-related data and presentation flows

Rules:

- do not mix unrelated business areas inside one feature
- keep repository, datasource, and other data-layer code inside `data`
- keep UI flows and state management inside `presentation`
- if a presentation flow grows too large, split it into subfeatures
- keep reusable logic outside the feature if many features need it

---

## Decision Rules

When deciding where code belongs, use these questions:

1. Is this a user-facing flow for one business area?

- Put it in `feature/<feature>`

2. Is this repository, datasource, model mapping, or other data-layer code?

- Put it in `feature/<feature>/data`

3. Is this a distinct UI flow such as create, update, detail, or list?

- Put it in `feature/<feature>/presentation/<subfeature>`

4. Is this the composable screen?

- Put it in `Screen.kt`

5. Is this the state holder and logic?

- Put it in `VM.kt`

6. Is this the screen state model?

- Put it in `UiState.kt`

---

## Quality Criteria

A valid feature structure should satisfy all of these:

- every feature lives in the `feature` folder
- every feature has both `data` and `presentation` folders
- every presentation subfeature has a `Screen`, `ViewModel`, and `UiState` file
- file names clearly identify the feature and subfeature
- screen logic stays separate from state logic
- state logic stays separate from UI state definitions
- the structure stays consistent across all features

---

## Completion Check

Before considering a feature complete, verify:

- the feature is located inside `feature/<feature>/`
- the feature has a `data` folder for repository and related data-layer code
- the feature has a `presentation` folder for UI flows
- each presentation subfeature has exactly `Screen`, `ViewModel`, and `UiState`
- the file names are consistent and descriptive
- the composable UI is inside the Screen file
- the logic is inside the ViewModel file
- the state model is inside the UiState file

---

## Example Prompts

Use this skill when you want to:

- create a new Android feature with subfeatures
- split a large feature into create/update/detail flows
- enforce consistent file naming for feature screens
- keep feature logic organized by subfeature

Example prompts:

- "Buat feature category dengan subfeature create, update, dan detail"
- "Susun folder feature todo supaya tiap subfeature punya Screen, VM, dan UiState"
- "Buat struktur feature auth dengan file yang konsisten"
- "Refactor feature ini supaya dipisah per subfeature"

---

## Notes

This skill is intentionally strict.

If a presentation subfeature exists, it should have all three required files.

If the project later grows, you can extend this skill with more detailed rules for:

- navigation per subfeature
- dependency injection into ViewModels
- data-layer repository patterns
- shared feature components
- test file placement

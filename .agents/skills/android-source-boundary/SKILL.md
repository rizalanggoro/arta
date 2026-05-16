---
name: android-source-boundary
description: Restricts an Android agent to edits inside app/src/main/java/<package> only. If a task needs changes outside that folder, the agent must stop and ask for confirmation or explain why it cannot proceed.
---

## Purpose

This skill protects the Android source tree by limiting all code changes to:

`app/src/main/java/<package>`

Use this skill when you want the agent to work only inside the Android app source package and avoid accidental edits anywhere else.

---

## Scope Rule

The agent MUST only modify files inside:

`app/src/main/java/<package>`

This includes:

- new Kotlin or Java source files
- refactors inside the package tree
- feature code
- shared app code that still lives under the package root

The agent MUST NOT modify anything outside that folder.

### Dependency Rule

The agent MUST NOT modify `build.gradle` files for the purpose of adding, updating, or removing dependencies.

This is a hard rule.

If a task requires a dependency change, the agent must stop and ask the user to add it manually.

The agent must never add dependencies on its own.

If dependency work is required, the correct behavior is:

1. explain which dependency is needed
2. explain why the change cannot be completed without it
3. ask the user to add it manually
4. continue only after the user confirms the dependency exists

---

## Hard Stop Rule

If the requested task requires changes outside `app/src/main/java/<package>`, or if it requires a dependency change in `build.gradle`, the agent must do one of these:

1. stop immediately and explain why the change cannot be completed inside the allowed scope, or
2. ask the user for explicit confirmation before making any out-of-scope change

If no confirmation is given, do not continue.

---

## Out-of-Scope Examples

Treat these as out of scope unless the user explicitly approves them:

- `app/build.gradle`
- `settings.gradle`
- `AndroidManifest.xml`
- `app/src/main/res`
- `app/src/test`
- `app/src/androidTest`
- `gradle.properties`
- `build.gradle` at the project root
- any documentation files
- any CI or tooling files
- any file outside `app/src/main/java/<package>`

---

## Decision Flow

Follow this sequence before editing:

1. identify the target files
2. verify every target file is under `app/src/main/java/<package>`
3. if all files are inside scope, proceed
4. if any file is outside scope, stop or ask for confirmation
5. if the user confirms, continue only with the confirmed out-of-scope change set

---

## Refactor Behavior

When a refactor would normally require moving code outside the allowed folder, the agent should not do it silently.

Instead:

- keep the change inside the allowed folder if possible
- otherwise stop and explain the boundary issue
- suggest the minimal external files that would need approval

Dependency additions are never to be applied by the agent. If a refactor needs a new library, stop and request manual dependency insertion from the user instead.

---

## Quality Criteria

A valid result should satisfy all of these:

- every edited file is inside `app/src/main/java/<package>`
- no supporting file outside that folder is changed without approval
- if a wider change is needed, the agent stops first
- the user always understands why scope expansion is needed
- dependency changes are requested from the user, never made by the agent

---

## Completion Check

Before finishing, verify:

- no file outside `app/src/main/java/<package>` was modified
- any required out-of-scope change was explicitly approved
- no dependency was added or modified by the agent
- the final response clearly states whether the work stayed within scope

---

## Example Prompts

- "Buat feature baru tapi jangan ubah file di luar app/src/main/java/<package>"
- "Refactor kode Android ini, tapi kalau perlu ubah file lain, stop dan minta konfirmasi"
- "Tambahkan logic ini hanya di source package Android"

---

## Notes

This skill is intentionally strict.

Dependency changes are forbidden for the agent.

If the work needs a new dependency, the agent must stop and instruct the user to add it manually before continuing.

If the task cannot be completed inside `app/src/main/java/<package>`, the correct behavior is to stop rather than expand scope silently.

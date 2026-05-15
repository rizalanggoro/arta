---
name: golang-swagger-docs
description: "Generate and maintain Swagger documentation for Go APIs with Swaggo. Use when you need to regenerate docs, sync swagger comments in handler.go files, verify path operations, or run swag init -g cmd/api/main.go --requiredByDefault after any Swagger comment change."
---

# Go Swagger Docs With Swaggo

Use this skill when maintaining Swagger/OpenAPI docs for a Go API that uses Swaggo.

## Workflow

1. Inspect the handler files that were changed.
2. Confirm the Swagger annotations are complete and consistent.
3. Make sure route annotations use `@Router`, not `@router`.
4. Regenerate docs from the project entrypoint with:

```bash
swag init -g cmd/api/main.go --requiredByDefault
```

5. Verify the generated docs include populated `paths` in `docs/swagger.json` and `docs/swagger.yaml`.
6. If generation fails or `paths` stays empty, inspect the handler comments first, then the generator command and source root.

## Required Checks

- Rerun generation every time Swagger comments are added or modified in any handler file.
- Keep `docs/` in sync with the current annotations.
- Confirm protected routes are documented with bearer auth if the API uses token-based authorization.
- Prefer explicit request and response DTOs in annotations.

## Comment Rules

- Use `@id` for operation names.
- Use `@tags` for feature grouping.
- Use `@accept` and `@produce` where relevant.
- Use `@param` for body, path, and query inputs.
- Use `@success` and `@failure` for documented responses.
- Use `@Router` with the public API path and HTTP method.

## Completion Criteria

A Swagger update is complete only when:

- `swag init -g cmd/api/main.go --requiredByDefault` succeeds.
- The generated `docs/swagger.json` contains the expected endpoint paths.
- The generated docs match the current handler annotations.
- No stale Swagger comments remain in handler files.

## Common Failure Signals

- `paths` is empty in generated JSON.
- Handler comments use `@router` instead of `@Router`.
- The generator is run from the wrong directory.
- A changed handler was not regenerated after annotation edits.

## Suggested Prompts

- "Generate Swagger docs after I changed handler comments."
- "Check why swagger paths are empty."
- "Regenerate docs with swaggo and verify the output."

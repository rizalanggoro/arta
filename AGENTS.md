# AGENTS.md

ARTA = personal finance & gold tracking app. Monorepo with two independent sub-projects, **no shared code**:

- `backend/` — Go API (Fiber v2, GORM, PostgreSQL, dig DI, gocron cron jobs)
- `android/` — Kotlin app (Compose + Material3, Navigation3, Hilt, Retrofit)

Product spec: `PRD.md`. `CLAUDE.md` is local-only (gitignored, not committed) — don't rely on it in fresh clones.

## Project skills (`.agents/skills/`)

Check the matching skill before implementing or refactoring features (android-*: screen/viewmodel/structure/domain; golang-*: feature files, DTO, handler, swagger). Caveat: skills are generic templates and some conflict with current code — e.g. they prescribe manual DI / plain ViewModel factories, but the app uses Hilt (`@HiltViewModel`, `@AndroidEntryPoint`, `assistedFactory` for route args). Match existing code when in doubt.

## Backend (`backend/`) — Go 1.25

Run from `backend/`:

- `make dev` — hot reload; requires `air` (`go install github.com/cosmtrek/air@latest`), build errors land in `tmp/build-errors.log`
- `make test` — `go test -v ./...`; **repo currently has no `*_test.go` files** (no-op)
- `make swagger` — `swag init -g cmd/api/main.go --requiredByDefault`; regenerates committed `docs/swagger.json`

Dev setup: `cp .env.example .env` → `docker-compose up -d postgres` → `make dev`. Server on `:3000`, Swagger UI at `/swagger/index.html`.

Architecture (entry `cmd/api/main.go` — dig container wires config → db → jwt → repos → handlers; cron started inside `container.Invoke`):

- `internal/feature/<name>/` — `handler.go` (routes; returns DTOs only), `repository.go` (GORM on `model` structs only), `request.go`, `response.go`
- `internal/domain/` ↔ `internal/model/` — 1:1 mapping via `From*Model()` / `ToModel()`
- `internal/dto/` — shared responses; errors are always `dto.Error{Code, Message}` JSON
- `internal/cron/{goldprice,fxrate}` — every 10 min, auto-started; handlers may inject these repos cross-feature for live gold prices
- New model → add to AutoMigrate (+ seed) list in `pkg/database/database.go`
- Auth: `middleware.GetUserID(c)` ("" if unauthenticated), `middleware.GetUserId(c)` (`(uint, error)`); verify wallet ownership via `repo.GetWalletOwnerID(walletID)` before mutating

## Android (`android/`) — Kotlin, Compose, Navigation3, Hilt

Run from `android/`:

- `make api` — regenerates the `openapi/` client from `backend/docs/swagger.json` (Makefile uses Windows `copy`/`rmdir`; `make api-linux` for Linux; requires `openapi-generator-cli` 7.22.0, pinned in `openapitools.json`)
- `./gradlew assembleDebug` — **`android/.env` with `BASE_URL` is required for any build** (debug and release bake it into BuildConfig; `.env` is gitignored)

**OpenAPI flow after any backend API change:** `make swagger` (backend) → `make api` (android) → commit all three: `backend/docs/swagger.json`, `android/swagger.json`, and the regenerated `openapi/` client — **`openapi/` is committed to git** (wired via `sourceSets` in `app/build.gradle.kts`).

Gotchas:

- `versionCode`/`versionName` live in `app/build.gradle.kts` (namespace `id.my.rizalanggoro.arta`)
- Release signing needs `app/release.jks` + env `KEYSTORE_PASSWORD`/`KEY_ALIAS`/`KEY_PASSWORD`; pushing to `main` triggers `.github/workflows/build-android.yaml` (release build, GH release `latest`, `POST /api/release` notification). Backend pushes to `main` also build+push an arm64 Docker image (`.github/workflows/build-backend-image.yaml`)

Architecture:

- `core/application/` — `ComposeApp.kt` root; Navigation3 `entry/` providers per feature + serializable `route/` classes
- `feature/<name>/presentation/<screen>/` — `<Screen>Screen.kt` + `<Screen>VM.kt` (`@HiltViewModel`; `assistedFactory` when VMs need route args) + `<Screen>UiState.kt`
- `core/data/` DataStore prefs (exposed as StateFlow), `core/network/` RetrofitProvider, `core/di/ApiModule.kt` (Hilt module), `core/event/` AppEventBus

## Code graph MCP — currently unavailable

`.opencode.json` / `.mcp.json` configure a `code-review-graph` MCP server, but its Python module (`code_review_graph`) is not installed, so the server fails to load and its tools (`semantic_search_nodes`, `query_graph`, `detect_changes`, …) do **not** exist in sessions. Explore with Grep/Glob/Read. (Repo also has no `.codegraph/` index.)
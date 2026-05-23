---
name: golang-project-structure
description: Defines a clean Go backend architecture with strict layered separation (cmd, internal, pkg, docs). Enforces Fiber for HTTP handling, GORM as the ORM, and DTO-only responses where DTO can embed domain structs.
---

## Project Structure

The project is organized into 4 main directories:

1. cmd
2. docs
3. internal
4. pkg

---

## cmd

Contains application entry points.

Examples:

- api/main.go
- seeder/main.go

Each subfolder is a separate executable.

---

## docs

Stores API documentation.

Common usage:

- Swagger / OpenAPI (swaggo generated docs)

---

## internal

Core application logic. Not exposed outside module.

Structure:

1. domain
2. dto
3. feature
4. model

---

### domain

Business-level data representation.

Rules:

- Mirrors `model` structure (1:1 file mapping)
- Used for internal data flow only
- Returned from repository layer
- Must NOT be returned directly to client

---

### dto

API response layer.

Rules:

- ONLY layer allowed for HTTP responses
- Every response MUST use DTO
- DTO may embed or contain `domain` structs directly as fields
- No business logic allowed

Important rule:

- Domain is allowed inside DTO struct composition
- But handler MUST return DTO only (never domain directly)

Example pattern:

- DTO contains field like: `User domain.User`

---

### feature

Feature-based modularization (auth, todo, category, etc).

Each feature contains:

- handler.go
- repository.go
- request.go
- response.go

Responsibilities:

- handler.go: HTTP handling + response assembly
- repository.go: database operations only, using GORM
- request.go: request payload definitions
- response.go: DTO definitions for API output

Rules:

- handler returns only DTO and uses Fiber
- repository never knows DTO
- repository only uses model and GORM

---

### model

Database models only.

Rules:

- Used exclusively in repository layer
- Direct mapping to database tables
- Must never be exposed outside repository

---

## pkg

Reusable shared utilities.

Examples:

- database connection
- logging
- helper functions

---

## Application Flow

1. Client sends request to Fiber handler
2. handler parses request using `request.go`
3. handler calls repository
   - input can be primitives or domain structs
4. repository executes database operations using GORM and `model`
   - repository MUST NOT use DTO
5. repository returns:
   - domain OR primitive data + error
6. handler constructs response DTO:
   - DTO may embed domain struct directly (no conversion required)
7. handler returns DTO only
8. client receives JSON response

---

## Critical Rules

- Fiber is the required HTTP framework for feature handlers
- GORM is the required ORM for repository implementations
- DTO is the ONLY allowed response type
- Domain must NEVER be returned directly from handler
- DTO MAY contain embedded domain structs
- Repository must never know DTO

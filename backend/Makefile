.PHONY: help build run test install-deps

help:
	@echo "ARTA Backend - Available commands:"
	@echo "  make install-deps   - Install Go dependencies"
	@echo "  make build          - Build the application"
	@echo "  make run            - Run the application"
	@echo "  make test           - Run tests"
	@echo "  make dev            - Run in development mode (with hot reload)"
	@echo "  make swagger        - Generate Swagger docs"
	@echo "  make clean          - Clean build artifacts"

install-deps:
	@echo "Installing dependencies..."
	go mod download
	go mod tidy

build:
	@echo "Building ARTA Backend..."
	go build -o bin/api cmd/api/main.go
	@echo "Build complete: bin/api"

run: build
	@echo "Running ARTA Backend..."
	./bin/api

test:
	@echo "Running tests..."
	go test -v ./...

dev:
	@echo "Running in development mode..."
	@echo "Note: Install 'air' for hot reload: go install github.com/cosmtrek/air@latest"
	air

swagger:
	@echo "Generating Swagger docs..."
	@echo "Note: Install 'swag': go install github.com/swaggo/swag/cmd/swag@latest"
	swag init -g cmd/api/main.go --requiredByDefault

clean:
	@echo "Cleaning build artifacts..."
	rm -rf bin/
	go clean

docker-build:
	docker build -t arta-api:latest .

docker-run:
	docker run -p 3000:3000 --env-file .env arta-api:latest

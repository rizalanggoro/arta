# ARTA Backend API

Backend server untuk aplikasi ARTA - Manajemen Keuangan dan Pencatatan Emas.

## Tech Stack

- **Language**: Go 1.21
- **Framework**: Fiber v2
- **Database**: PostgreSQL
- **ORM**: GORM
- **Authentication**: JWT (JSON Web Token)
- **Documentation**: Swagger/OpenAPI (swaggo)

## Project Structure

```
backend/
├── cmd/
│   └── api/
│       └── main.go              # Application entry point
├── docs/                        # Swagger documentation
├── internal/
│   ├── domain/                  # Business logic data representation
│   ├── dto/                     # API response DTOs
│   ├── feature/                 # Feature modules
│   │   ├── auth/                # Authentication feature
│   │   ├── transaction/         # Transaction feature
│   │   ├── gold/                # Gold tracking feature
│   │   ├── category/            # Category feature
│   │   └── user/                # User profile feature
│   └── model/                   # Database models (GORM)
├── pkg/
│   ├── config/                  # Configuration management
│   ├── database/                # Database connection & migrations
│   ├── logger/                  # Logging utilities
│   ├── jwt/                     # JWT token operations
│   ├── middleware/              # HTTP middlewares
│   └── util/                    # Helper utilities
├── go.mod                       # Go module definition
├── .env.example                 # Environment variables example
├── Makefile                     # Common commands
└── README.md                    # This file
```

## Installation

### Prerequisites

- Go 1.21 or higher
- PostgreSQL 12 or higher
- Git

### Setup

1. Clone the repository:

```bash
cd backend
```

2. Install dependencies:

```bash
make install-deps
# or manually
go mod download
```

3. Copy environment file:

```bash
cp .env.example .env
```

4. Update `.env` with your configuration:

```
SERVER_PORT=3000
DB_HOST=localhost
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=postgres
DB_NAME=arta
JWT_SECRET=your-secret-key
```

5. Create PostgreSQL database:

```bash
createdb arta
```

## Development

### Run Application

```bash
make run
# or for development with hot reload
make dev
# requires: go install github.com/cosmtrek/air@latest
```

The API will be available at `http://localhost:3000`

### Build

```bash
make build
```

### Generate Swagger Documentation

```bash
make swagger
# requires: go install github.com/swaggo/swag/cmd/swag@latest
```

### Run Tests

```bash
make test
```

## API Documentation

Once the application is running, visit:

- Swagger UI: `http://localhost:3000/swagger/index.html`
- API Docs: `http://localhost:3000/docs`

## Features

### Authentication

- User registration
- User login with JWT token
- Token validation for protected endpoints
- Logout & token revocation

### Transaction Management

- Create transactions (income/expense)
- Read transaction history
- Update transactions
- Delete transactions
- Filter by date, category, type

### Gold Tracking

- Record gold holdings (multiple types)
- Track gold prices
- View gold summary
- Calculate total value

### Category Management

- Default categories
- Custom user categories
- Edit categories
- Delete custom categories

### User Profile

- View profile
- Update profile
- Change password
- Account management

## API Endpoints

### Auth

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/logout` - Logout user
- `POST /api/auth/forgot-password` - Request password reset

### Transactions

- `GET /api/transactions` - List transactions (with filters)
- `POST /api/transactions` - Create transaction
- `GET /api/transactions/:id` - Get transaction detail
- `PUT /api/transactions/:id` - Update transaction
- `DELETE /api/transactions/:id` - Delete transaction

### Gold

- `GET /api/gold` - List gold holdings
- `POST /api/gold` - Record gold holding
- `GET /api/gold/:id` - Get gold detail
- `PUT /api/gold/:id` - Update gold data
- `DELETE /api/gold/:id` - Delete gold data
- `GET /api/gold/summary` - Get gold summary

### Categories

- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `PUT /api/categories/:id` - Update category
- `DELETE /api/categories/:id` - Delete category

### User

- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update user profile
- `POST /api/user/change-password` - Change password
- `DELETE /api/user/account` - Delete account

## Authentication

All protected endpoints require JWT token in Authorization header:

```
Authorization: Bearer <jwt_token>
```

Token is obtained from login endpoint and stored in client (SharedPreferences on Android).

## Database Schema

The application uses the following main tables:

- `users` - User accounts
- `sessions` - JWT session tokens
- `categories` - Transaction categories
- `transactions` - Financial transactions
- `gold` - Gold holdings
- `gold_prices` - Gold price history

## Contributing

See main project CONTRIBUTING guide.

## License

See main project LICENSE file.

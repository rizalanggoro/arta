package dto

import (
	"github.com/artafinance/backend/internal/domain"
)

// User represents user response DTO
// @name User
type User struct {
	Data domain.User `json:"data"`
}

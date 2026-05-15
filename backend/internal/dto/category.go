package dto

import "github.com/artafinance/backend/internal/domain"

// Category represents category response DTO
// @name Category
type Category struct {
	Data domain.Category `json:"data"`
}

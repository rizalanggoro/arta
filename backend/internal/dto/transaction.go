package dto

import "github.com/artafinance/backend/internal/domain"

// Transaction represents transaction response DTO
// @name Transaction
type Transaction struct {
	Data     domain.Transaction `json:"data"`
	Category *domain.Category   `json:"category,omitempty"`
}

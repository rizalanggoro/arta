package dto

import "github.com/artafinance/backend/internal/domain"

// Wallet represents wallet response DTO
// @name Wallet
type Wallet struct {
	Data domain.Wallet `json:"data"`
}

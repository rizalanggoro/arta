package dto

import "github.com/artafinance/backend/internal/domain"

// Category represents category response DTO
// @name Category
type Category struct {
	Data             domain.Category `json:"data"`
	TotalAmount      float64         `json:"total_amount" format:"double"`
	TransactionCount int             `json:"transaction_count"`
}

package dto

import "github.com/artafinance/backend/internal/domain"

// GoldTaxPreference represents a user's gold tax preference response DTO.
// deprecated
// @name GoldTaxPreference
type GoldTaxPreference struct {
	ID        uint    `json:"id"`
	UserID    uint    `json:"user_id"`
	Carat     float64 `json:"carat"`
	TaxRate   float64 `json:"tax_rate"`
	CreatedAt string  `json:"created_at"`
	UpdatedAt string  `json:"updated_at"`
}

// @name GoldTaxDTO
type GoldTax struct {
	Data      domain.GoldTaxPreference `json:"data"`
	SellPrice float64                  `json:"sell_price" format:"double"`
}

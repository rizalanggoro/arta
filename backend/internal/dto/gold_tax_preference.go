package dto

// GoldTaxPreference represents a user's gold tax preference response DTO.
// @name GoldTaxPreference
type GoldTaxPreference struct {
	ID        uint    `json:"id"`
	UserID    uint    `json:"user_id"`
	Carat     float64 `json:"carat"`
	TaxRate   float64 `json:"tax_rate"`
	CreatedAt string  `json:"created_at"`
	UpdatedAt string  `json:"updated_at"`
}

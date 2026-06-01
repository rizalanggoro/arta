package dto

import "github.com/artafinance/backend/internal/domain"

// Gold represents gold holding response DTO
// @name Gold
type Gold struct {
	Data      domain.Gold `json:"data"`
	SellPrice float64     `json:"sell_price" format:"double"`
	Profit    float64     `json:"profit" format:"double"`
}

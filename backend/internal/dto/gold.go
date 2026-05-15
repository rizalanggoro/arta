package dto

import "github.com/artafinance/backend/internal/domain"

// Gold represents gold holding response DTO
// @name Gold
type Gold struct {
	Data domain.Gold `json:"data"`
}

// GoldSummary represents gold summary response DTO
// @name GoldSummary
type GoldSummary struct {
	TotalGrams float64                `json:"total_grams"`
	TotalValue float64                `json:"total_value"`
	ByType     map[string]interface{} `json:"by_type"`
}

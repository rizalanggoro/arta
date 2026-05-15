package gold

import "github.com/artafinance/backend/internal/dto"

// CreateGoldRes response for create
type CreateGoldRes struct {
	dto.Gold
} // @name CreateGoldRes

// GetGoldRes response for get
type GetGoldRes struct {
	dto.Gold
} // @name GetGoldRes

// UpdateGoldRes response for update
type UpdateGoldRes struct {
	dto.Gold
} // @name UpdateGoldRes

// DeleteGoldRes response for delete
type DeleteGoldRes struct {
	Message string `json:"message"`
} // @name DeleteGoldRes

// ListGoldsRes response for list
type ListGoldsRes struct {
	Golds []dto.Gold `json:"golds"`
}

// GoldSummaryRes response for summary
type GoldSummaryRes struct {
	dto.GoldSummary
} // @name GoldSummaryRes

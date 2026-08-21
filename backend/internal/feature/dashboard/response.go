package dashboard

import "github.com/artafinance/backend/internal/dto"

// CashDashboardRes response for cash dashboard.
type CashDashboardRes struct {
	dto.CashDashboard
} // @name CashDashboardRes

// GoldDashboardRes response for gold dashboard.
type GoldDashboardRes struct {
	Data dto.GoldDashboard `json:"data"`
} // @name GoldDashboardRes

// PriceHistoryRes response for price history.
type PriceHistoryRes struct {
	Data []dto.PricePoint `json:"data"`
} // @name PriceHistoryRes

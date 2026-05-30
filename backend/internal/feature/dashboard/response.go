package dashboard

import "github.com/artafinance/backend/internal/dto"

// CashDashboardRes response for cash dashboard.
type CashDashboardRes struct {
	Data dto.CashDashboard `json:"data"`
} // @name CashDashboardRes

// GoldDashboardRes response for gold dashboard.
type GoldDashboardRes struct {
	Data dto.GoldDashboard `json:"data"`
} // @name GoldDashboardRes

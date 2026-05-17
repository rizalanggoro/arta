package dashboard

import "github.com/artafinance/backend/internal/dto"

// CashDashboardRes response for cash dashboard.
type CashDashboardRes struct {
	dto.CashDashboard
} // @name CashDashboardRes

// GoldDashboardRes response for gold dashboard.
type GoldDashboardRes struct {
	dto.GoldDashboard
} // @name GoldDashboardRes

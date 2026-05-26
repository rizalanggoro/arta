package dto

import "github.com/artafinance/backend/internal/domain"

// CashDashboard represents the cash home dashboard response DTO.
// @name CashDashboard
type CashDashboard struct {
	ActiveWalletName string `json:"active_wallet_name"`
	FinancialSummary struct {
		CurrentBalance float64 `json:"current_balance"`
		TodayIncome    float64 `json:"today_income"`
		TodayExpense   float64 `json:"today_expense"`
	} `json:"financial_summary"`
	RecentTransactions []struct {
		Data     domain.Transaction `json:"data"`
		Category domain.Category    `json:"category"`
	} `json:"recent_transactions"`
}

// GoldDashboard represents the gold home dashboard response DTO.
// @name GoldDashboard
type GoldDashboard struct {
	TotalAsset     float64                    `json:"total_asset"`
	TotalBuyPrice  float64                    `json:"total_buy_price"`
	Profit         float64                    `json:"profit"`
	TotalWeight    float64                    `json:"total_weight"`
	TotalGoldItems int                        `json:"total_gold_items"`
	GoldPrice      domain.GoldPrice           `json:"gold_price"`
	FxRate         domain.FxRate              `json:"fx_rate"`
	RecentGolds    []Gold                     `json:"recent_golds"`
	TaxPreferences []domain.GoldTaxPreference `json:"tax_preferences"`
}

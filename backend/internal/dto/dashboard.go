package dto

import "github.com/artafinance/backend/internal/domain"

// CashDashboard represents the cash home dashboard response DTO.
// @name CashDashboard
type CashDashboard struct {
	CurrentBalance     float64       `json:"current_balance" format:"double"`
	TotalIncome        float64       `json:"total_income" format:"double"`
	TotalExpense       float64       `json:"total_expense" format:"double"`
	LatestTransactions []Transaction `json:"latest_transactions"`
}

// GoldDashboard represents the gold home dashboard response DTO.
// @name GoldDashboard
type GoldDashboard struct {
	TotalAsset     float64          `json:"total_asset" format:"double"`
	TotalBuyPrice  float64          `json:"total_buy_price" format:"double"`
	Profit         float64          `json:"profit" format:"double"`
	TotalWeight    float64          `json:"total_weight" format:"double"`
	TotalGoldItems int              `json:"total_gold_items"`
	GoldPrice      domain.GoldPrice `json:"gold_price"`
	FxRate         domain.FxRate    `json:"fx_rate"`
	RetailPrice    float64          `json:"retail_price" format:"double"`
	LatestGolds    []Gold           `json:"latest_golds"`
	GoldTaxes      []GoldTax        `json:"gold_taxes"`
}

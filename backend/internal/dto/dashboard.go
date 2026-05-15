package dto

// Dashboard represents dashboard overview response DTO
// @name Dashboard
type Dashboard struct {
	FinancialSummary struct {
		CurrentBalance float64 `json:"current_balance"`
		MonthlyIncome  float64 `json:"monthly_income"`
		MonthlyExpense float64 `json:"monthly_expense"`
	} `json:"financial_summary"`
	GoldSummary        *GoldSummary   `json:"gold_summary"`
	RecentTransactions []*Transaction `json:"recent_transactions"`
}

package dashboard

import (
	"math"
	"strconv"
	"time"

	"github.com/artafinance/backend/internal/cron/fxrate"
	"github.com/artafinance/backend/internal/cron/goldprice"
	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	categoryfeature "github.com/artafinance/backend/internal/feature/category"
	goldfeature "github.com/artafinance/backend/internal/feature/gold"
	transactionfeature "github.com/artafinance/backend/internal/feature/transaction"
	walletfeature "github.com/artafinance/backend/internal/feature/wallet"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

const gramsPerTroyOunce = 31.1034768

// Handler exposes dashboard HTTP endpoints.
type Handler struct {
	walletRepo      *walletfeature.Repository
	goldRepo        *goldfeature.Repository
	goldPriceRepo   *goldprice.Repository
	fxRateRepo      *fxrate.Repository
	transactionRepo *transactionfeature.Repository
	categoryRepo    *categoryfeature.Repository
	jwtMgr          *jwt.Manager
	checker         middleware.TokenStatusChecker
}

// NewHandler creates a new dashboard handler.
func NewHandler(
	walletRepo *walletfeature.Repository,
	goldRepo *goldfeature.Repository,
	goldPriceRepo *goldprice.Repository,
	fxRateRepo *fxrate.Repository,
	transactionRepo *transactionfeature.Repository,
	categoryRepo *categoryfeature.Repository,
	jwtMgr *jwt.Manager,
	checker middleware.TokenStatusChecker,
) *Handler {
	return &Handler{
		walletRepo:      walletRepo,
		goldRepo:        goldRepo,
		goldPriceRepo:   goldPriceRepo,
		fxRateRepo:      fxRateRepo,
		transactionRepo: transactionRepo,
		categoryRepo:    categoryRepo,
		jwtMgr:          jwtMgr,
		checker:         checker,
	}
}

// RegisterRoutes registers dashboard routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/dashboard")
	protected := group.Use(middleware.AuthMiddleware(h.jwtMgr, h.checker))
	protected.Get("/cash", h.cash)
	protected.Get("/gold", h.gold)
}

// @Summary Get gold dashboard overview
// @Description Return the active gold wallet name, asset summary, current prices, and the latest 5 gold entries.
// @Tags dashboard
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Success 200 {object} GoldDashboardRes
// @Failure 401 {object} dto.Error
// @Failure 404 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/dashboard/gold [get]
func (h *Handler) gold(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	wallets, err := h.walletRepo.GetWalletsByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	activeWallet, ok := selectActiveGoldWallet(wallets)
	if !ok {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: "gold wallet not found"})
	}

	golds, err := h.goldRepo.GetGoldsByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	latestGoldPrice, err := h.goldPriceRepo.GetLatest()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	latestFxRate, err := h.fxRateRepo.GetLatest()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	totalWeight := 0.0
	buyPrice := 0.0
	recentGolds := make([]struct {
		Data domain.Gold `json:"data"`
	}, 0, min(len(golds), 5))
	for i := range golds {
		goldItem := golds[i]
		totalWeight += goldItem.Grams
		buyPrice += goldItem.Price
		if len(recentGolds) < 5 {
			recentGolds = append(recentGolds, struct {
				Data domain.Gold `json:"data"`
			}{Data: goldItem})
		}
	}

	latestDollarPrice := float64(latestFxRate.Rate)
	latestGoldPricePerGramIDR := (latestGoldPrice.PricePerOunceUSD * latestDollarPrice) / gramsPerTroyOunce
	totalAsset := math.Round(totalWeight*latestGoldPricePerGramIDR*100) / 100
	profit := math.Round((totalAsset-buyPrice)*100) / 100

	res := GoldDashboardRes{}
	res.ActiveWalletName = activeWallet.Name
	res.TotalAsset = totalAsset
	res.BuyPrice = math.Round(buyPrice*100) / 100
	res.Profit = profit
	res.TotalWeight = math.Round(totalWeight*100) / 100
	res.TotalGoldItems = len(golds)
	res.LatestDollarPrice = latestDollarPrice
	res.LatestGoldPricePerGramIDR = math.Round(latestGoldPricePerGramIDR*100) / 100
	res.RecentGolds = recentGolds

	return c.Status(fiber.StatusOK).JSON(res)
}

// @Summary Get cash dashboard overview
// @Description Return the active cash wallet name, balance summary, today totals, and the latest 5 transactions.
// @Tags dashboard
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Success 200 {object} CashDashboardRes
// @Failure 401 {object} dto.Error
// @Failure 404 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/dashboard/cash [get]
func (h *Handler) cash(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	wallets, err := h.walletRepo.GetWalletsByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	activeWallet, ok := selectActiveCashWallet(wallets)
	if !ok {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: "cash wallet not found"})
	}

	categories, err := h.categoryRepo.GetCategoriesByUserID(uint(parsedUserID), "")
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	categoryByID := make(map[uint]domain.Category, len(categories))
	for i := range categories {
		categoryByID[categories[i].ID] = categories[i]
	}

	transactions, err := h.transactionRepo.GetTransactionsByWalletID(activeWallet.ID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	now := time.Now()
	currentBalance := 0.0
	todayIncome := 0.0
	todayExpense := 0.0
	recentTransactions := make([]struct {
		Data     domain.Transaction `json:"data"`
		Category domain.Category    `json:"category"`
	}, 0, min(len(transactions), 5))

	for i := range transactions {
		transaction := transactions[i]
		category := categoryByID[transaction.CategoryID]
		isIncome := category.Type == "income"

		if isIncome {
			currentBalance += transaction.Amount
		} else {
			currentBalance -= transaction.Amount
		}

		if isSameDay(transaction.Date, now) {
			if isIncome {
				todayIncome += transaction.Amount
			} else {
				todayExpense += transaction.Amount
			}
		}

		if len(recentTransactions) < 5 {
			recentTransactions = append(recentTransactions, struct {
				Data     domain.Transaction `json:"data"`
				Category domain.Category    `json:"category"`
			}{Data: transaction, Category: category})
		}
	}

	res := CashDashboardRes{}
	res.ActiveWalletName = activeWallet.Name
	res.FinancialSummary.CurrentBalance = currentBalance
	res.FinancialSummary.TodayIncome = todayIncome
	res.FinancialSummary.TodayExpense = todayExpense
	res.RecentTransactions = recentTransactions

	return c.Status(fiber.StatusOK).JSON(res)
}

func selectActiveCashWallet(wallets []domain.Wallet) (domain.Wallet, bool) {
	for i := range wallets {
		if wallets[i].Type == "cash_savings" {
			return wallets[i], true
		}
	}
	return domain.Wallet{}, false
}

func selectActiveGoldWallet(wallets []domain.Wallet) (domain.Wallet, bool) {
	for i := range wallets {
		if wallets[i].Type == "gold_savings" {
			return wallets[i], true
		}
	}
	return domain.Wallet{}, false
}

func isSameDay(a, b time.Time) bool {
	ay, am, ad := a.In(time.Local).Date()
	by, bm, bd := b.In(time.Local).Date()
	return ay == by && am == bm && ad == bd
}

func min(a, b int) int {
	if a < b {
		return a
	}
	return b
}

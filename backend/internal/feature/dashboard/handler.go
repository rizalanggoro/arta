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

// @ID GetGoldDashboard
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

	var activeWallet domain.Wallet
	ok := false
	for i := range wallets {
		if wallets[i].Type == "gold_savings" {
			activeWallet = wallets[i]
			ok = true
			break
		}
	}
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

	taxPreferences, err := h.goldRepo.GetTaxPreferencesByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	taxByCarat := make(map[float64]float64, len(taxPreferences))
	for i := range taxPreferences {
		taxByCarat[taxPreferences[i].Carat] = taxPreferences[i].TaxRate
	}

	latestFxRate, err := h.fxRateRepo.GetLatest()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	latestDollarPrice := float64(latestFxRate.Rate)
	latestGoldPricePerGramIDR := (latestGoldPrice.PricePerOunceUSD * latestDollarPrice) / gramsPerTroyOunce

	totalWeight := 0.0
	buyPrice := 0.0
	recentGoldsLimit := 5
	if len(golds) < recentGoldsLimit {
		recentGoldsLimit = len(golds)
	}

	recentGolds := make([]dto.Gold, 0, recentGoldsLimit)
	for i := range golds {
		goldItem := golds[i]
		totalWeight += goldItem.Grams
		buyPrice += goldItem.Price
		if len(recentGolds) < 5 {
			taxRate := taxByCarat[goldItem.Carat]
			sellPrice := goldItem.Grams * latestGoldPricePerGramIDR * (1 - (taxRate / 100))
			profit := sellPrice - goldItem.Price
			recentGolds = append(recentGolds, dto.Gold{
				Data:      goldItem,
				SellPrice: math.Round(sellPrice*100) / 100,
				Profit:    math.Round(profit*100) / 100,
			})
		}
	}
	totalAsset := 0.0
	for i := range golds {
		taxRate := taxByCarat[golds[i].Carat]
		sellValue := golds[i].Grams * latestGoldPricePerGramIDR * (1 - (taxRate / 100))
		totalAsset += sellValue
	}
	totalAsset = math.Round(totalAsset*100) / 100
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

// @ID GetCashDashboard
// @Summary Get cash dashboard overview
// @Description Return the active cash wallet name, balance summary, today totals, and the latest 5 transactions.
// @Tags dashboard
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Param wallet_id query int false "Selected cash wallet ID"
// @Success 200 {object} CashDashboardRes
// @Failure 400 {object} dto.Error
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

	walletIDParam := c.Query("wallet_id")
	var activeWallet domain.Wallet
	var ok bool

	if walletIDParam != "" {
		walletID, parseErr := strconv.ParseUint(walletIDParam, 10, 64)
		if parseErr != nil {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "wallet_id must be a valid number"})
		}

		for i := range wallets {
			if wallets[i].ID == uint(walletID) && wallets[i].Type == "cash_savings" {
				activeWallet = wallets[i]
				ok = true
				break
			}
		}
	} else {
		for i := range wallets {
			if wallets[i].Type == "cash_savings" {
				activeWallet = wallets[i]
				ok = true
				break
			}
		}
	}

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
	recentTransactionsLimit := 5
	if len(transactions) < recentTransactionsLimit {
		recentTransactionsLimit = len(transactions)
	}

	recentTransactions := make([]struct {
		Data     domain.Transaction `json:"data"`
		Category domain.Category    `json:"category"`
	}, 0, recentTransactionsLimit)

	for i := range transactions {
		transaction := transactions[i]
		category := categoryByID[transaction.CategoryID]
		isIncome := category.Type == "income"

		if isIncome {
			currentBalance += transaction.Amount
		} else {
			currentBalance -= transaction.Amount
		}

		ty, tm, td := transaction.Date.In(time.Local).Date()
		ny, nm, nd := now.In(time.Local).Date()
		if ty == ny && tm == nm && td == nd {
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

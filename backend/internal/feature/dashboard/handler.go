package dashboard

import (
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

// @ID 					GetGoldDashboard
// @Tags 				dashboard
// @Accept 			json
// @Produce 		json
// @Param 			Authorization header string true "Bearer token"
// @param 			wallet_id query int true "wallet_id"
// @Success 		200 {object} GoldDashboardRes
// @Failure 		401 {object} dto.Error
// @Failure 		404 {object} dto.Error
// @Failure 		500 {object} dto.Error
// @Router 			/api/dashboard/gold [get]
func (h *Handler) gold(c *fiber.Ctx) error {
	userIdStr := middleware.GetUserID(c)
	if userIdStr == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
			Code:    fiber.StatusUnauthorized,
			Message: "unauthorized",
		})
	}

	userId, err := strconv.Atoi(userIdStr)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	walletId := c.QueryInt("wallet_id", 0)
	if walletId == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: "wallet_id is required and must be a valid number",
		})
	}

	goldPrice, err := h.goldPriceRepo.GetLatest()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	fxRate, err := h.fxRateRepo.GetLatest()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	golds, err := h.goldRepo.GetAll(goldfeature.GetAllFilter{
		WalletId: uint(walletId),
		OrderBy:  "date",
		OrderDir: "desc",
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	taxPreferences, err := h.goldRepo.GetTaxPreferencesByUserID(uint(userId))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	mappedTax := map[uint]domain.GoldTaxPreference{}
	for _, tp := range taxPreferences {
		mappedTax[uint(tp.Carat)] = tp
	}

	mappedGolds := make([]dto.Gold, len(*golds))

	goldPricePerGramIDR := goldPrice.PricePerOunceUSD / gramsPerTroyOunce * float64(fxRate.Rate)

	totalAsset := 0.0
	totalBuyPrice := 0.0
	totalWeight := 0.0
	totalGoldItems := len(*golds)
	for index, gold := range *golds {
		totalBuyPrice += float64(gold.Price)
		totalWeight += gold.Grams

		tax := 1.0
		if _, exists := mappedTax[uint(gold.Carat)]; exists {
			tax = 1 - mappedTax[uint(gold.Carat)].TaxRate/100
		}

		sellPrice := goldPricePerGramIDR * gold.Grams * (gold.Carat / 24.0) * tax
		totalAsset += sellPrice

		mappedGolds[index] = dto.Gold{
			Data:      gold,
			SellPrice: sellPrice,
			Profit:    sellPrice - float64(gold.Price),
		}
	}

	return c.Status(fiber.StatusOK).JSON(GoldDashboardRes{
		Data: dto.GoldDashboard{
			TotalAsset:     totalAsset,
			TotalBuyPrice:  totalBuyPrice,
			Profit:         totalAsset - totalBuyPrice,
			TotalWeight:    totalWeight,
			TotalGoldItems: totalGoldItems,
			GoldPrice:      *goldPrice,
			FxRate:         *fxRate,
			RecentGolds:    mappedGolds[:min(5, len(mappedGolds))],
			TaxPreferences: taxPreferences,
		},
	})
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
		return c.Status(fiber.StatusUnauthorized).
			JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).
			JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	wallets, err := h.walletRepo.GetWalletsByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).
			JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	walletIDParam := c.Query("wallet_id")
	var activeWallet domain.Wallet
	var ok bool

	if walletIDParam != "" {
		walletID, parseErr := strconv.ParseUint(walletIDParam, 10, 64)
		if parseErr != nil {
			return c.Status(fiber.StatusBadRequest).
				JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "wallet_id must be a valid number"})
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
		return c.Status(fiber.StatusNotFound).
			JSON(dto.Error{Code: fiber.StatusNotFound, Message: "cash wallet not found"})
	}

	categories, err := h.categoryRepo.GetCategoriesByUserID(uint(parsedUserID), "")
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).
			JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	categoryByID := make(map[uint]domain.Category, len(categories))
	for i := range categories {
		categoryByID[categories[i].ID] = categories[i]
	}

	transactions, err := h.transactionRepo.GetTransactionsByWalletID(activeWallet.ID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).
			JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
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

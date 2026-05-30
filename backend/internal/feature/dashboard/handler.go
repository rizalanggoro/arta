package dashboard

import (
	"strconv"

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
// @Param wallet_id query int true "wallet_id"
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
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
			Code:    fiber.StatusUnauthorized,
			Message: "unauthorized",
		})
	}

	walletId := c.QueryInt("wallet_id", 0)
	if walletId == 0 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: "wallet_id is required and must be a valid number",
		})
	}

	currentBalance, err := h.transactionRepo.GetCurrentBalance(transactionfeature.GetCurrentBalanceFilter{
		WalletId: uint(walletId),
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	totalIncome, totalExpense, err := h.transactionRepo.GetTotalIncomeExpense(transactionfeature.GetTotalIncomeExpenseFilter{
		WalletId: uint(walletId),
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	latestTransactions, err := h.transactionRepo.GetAll(&transactionfeature.GetAllFilter{
		WalletId:        uint(walletId),
		IncludeCategory: true,
		Limit:           5,
		OrderBy:         "date",
		OrderDirection:  "desc",
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	return c.Status(fiber.StatusOK).JSON(CashDashboardRes{
		Data: dto.CashDashboard{
			CurrentBalance:     *currentBalance,
			TotalIncome:        *totalIncome,
			TotalExpense:       *totalExpense,
			LatestTransactions: latestTransactions,
		},
	})
}

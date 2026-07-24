package dashboard

import (
	"strconv"
	"time"

	"github.com/artafinance/backend/internal/cron/fxrate"
	"github.com/artafinance/backend/internal/cron/goldprice"
	"github.com/artafinance/backend/internal/dto"
	categoryfeature "github.com/artafinance/backend/internal/feature/category"
	goldfeature "github.com/artafinance/backend/internal/feature/gold"
	transactionfeature "github.com/artafinance/backend/internal/feature/transaction"
	walletfeature "github.com/artafinance/backend/internal/feature/wallet"
	"github.com/artafinance/backend/pkg/config"
	"github.com/artafinance/backend/pkg/constant"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

// Handler exposes dashboard HTTP endpoints.
type Handler struct {
	walletRepo        *walletfeature.Repository
	goldRepo          *goldfeature.Repository
	goldPriceRepo     *goldprice.Repository
	fxRateRepo        *fxrate.Repository
	transactionRepo   *transactionfeature.Repository
	categoryRepo      *categoryfeature.Repository
	jwtMgr            *jwt.Manager
	config            *config.Config
	dashboardGoldRepo *DashboardGoldRepository
	goldTaxRepo       *goldfeature.GoldTaxRepository
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
	config *config.Config,
	dashboardGoldRepo *DashboardGoldRepository,
	goldTaxRepo *goldfeature.GoldTaxRepository,
) *Handler {
	return &Handler{
		walletRepo:        walletRepo,
		goldRepo:          goldRepo,
		goldPriceRepo:     goldPriceRepo,
		fxRateRepo:        fxRateRepo,
		transactionRepo:   transactionRepo,
		categoryRepo:      categoryRepo,
		jwtMgr:            jwtMgr,
		config:            config,
		dashboardGoldRepo: dashboardGoldRepo,
		goldTaxRepo:       goldTaxRepo,
	}
}

// RegisterRoutes registers dashboard routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/dashboard")
	protected := group.Use(middleware.AuthMiddleware(h.jwtMgr))
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

	totalSellPrice, err := h.dashboardGoldRepo.GetTotalSellPrice(GetTotalSellPriceFilter{
		WalletId: uint(walletId),
		UserId:   uint(userId),
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	totalBuyPrice, err := h.dashboardGoldRepo.GetTotalBuyPrice(GetTotalBuyPriceFilter{
		WalletId: uint(walletId),
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	totalWeight, err := h.dashboardGoldRepo.GetTotalWeight(GetTotalWeightFilter{
		WalletId: uint(walletId),
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	itemCount, err := h.dashboardGoldRepo.GetItemCount(GetItemCountFilter{
		WalletId: uint(walletId),
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	golds, err := h.goldRepo.GetAll(goldfeature.GetAllFilter{
		UserId:   uint(userId),
		WalletId: uint(walletId),
		OrderBy:  "date",
		OrderDir: "desc",
		Limit:    5,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	goldTaxes, err := h.goldTaxRepo.GetAllGoldTaxes(goldfeature.GetAllGoldTaxesFilter{
		UserId: uint(userId),
	})

	retailPrice := goldPrice.PricePerOunceUSD / constant.GramsPerTroyOunce * float64(fxRate.Rate) * h.config.GoldRetailMultiplier

	return c.Status(fiber.StatusOK).JSON(GoldDashboardRes{
		Data: dto.GoldDashboard{
			TotalAsset:     *totalSellPrice,
			TotalBuyPrice:  *totalBuyPrice,
			Profit:         *totalSellPrice - *totalBuyPrice,
			TotalWeight:    *totalWeight,
			TotalGoldItems: *itemCount,
			GoldPrice:      *goldPrice,
			FxRate:         *fxRate,
			RetailPrice:    retailPrice,
			LatestGolds:    golds,
			GoldTaxes:      goldTaxes,
		},
	})
}

// @ID 					GetCashDashboard
// @Tags 				dashboard
// @Accept 			json
// @Produce 		json
// @Param 			Authorization header string true "Bearer token"
// @Param 			wallet_id query int true "wallet_id"
// @param 			start_date query string true "start_date"
// @param 			end_date query string true "end_date"
// @Success 		200 {object} CashDashboardRes
// @Failure 		400 {object} dto.Error
// @Failure 		401 {object} dto.Error
// @Failure 		404 {object} dto.Error
// @Failure 		500 {object} dto.Error
// @Router 			/api/dashboard/cash [get]
func (h *Handler) cash(c *fiber.Ctx) error {
	strUserId := middleware.GetUserID(c)
	if strUserId == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
			Code:    fiber.StatusUnauthorized,
			Message: "unauthorized",
		})
	}

	userId, err := strconv.Atoi(strUserId)
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

	startDateStr := c.Query("start_date")
	endDateStr := c.Query("end_date")

	startDate, err := time.Parse("2006-01-02", startDateStr)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: err.Error(),
		})
	}

	endDate, err := time.Parse("2006-01-02", endDateStr)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: err.Error(),
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
		WalletId:  uint(walletId),
		StartDate: startDate,
		EndDate:   endDate,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	// Previous period: same duration, ending at startDate
	duration := endDate.Sub(startDate)
	prevStart := startDate.Add(-duration)
	prevEnd := startDate

	prevIncome, prevExpense, err := h.transactionRepo.GetTotalIncomeExpense(transactionfeature.GetTotalIncomeExpenseFilter{
		WalletId:  uint(walletId),
		StartDate: prevStart,
		EndDate:   prevEnd,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	categories, err := h.categoryRepo.GetAll(categoryfeature.GetAllCategoriesFilter{
		UserId:       uint(userId),
		IncludeStats: true,
		WalletId:     uint(walletId),
		StartDate:    startDate,
		EndDate:      endDate,
	})

	return c.Status(fiber.StatusOK).JSON(CashDashboardRes{
		dto.CashDashboard{
			CurrentBalance:    *currentBalance,
			TotalIncome:       *totalIncome,
			TotalExpense:      *totalExpense,
			PrevPeriodIncome:  *prevIncome,
			PrevPeriodExpense: *prevExpense,
			LatestCategories:  categories,
		},
	})
}

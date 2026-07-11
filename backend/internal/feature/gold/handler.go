package gold

import (
	"strconv"
	"time"

	"github.com/artafinance/backend/internal/cron/fxrate"
	"github.com/artafinance/backend/internal/cron/goldprice"
	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

func isValidGoldType(value string) bool {
	return value == domain.GoldTypePure || value == domain.GoldTypeJewelry
}

// Handler exposes gold HTTP endpoints.
type Handler struct {
	repo          *Repository
	fxRepo        *fxrate.Repository
	goldPriceRepo *goldprice.Repository
	jwtMgr        *jwt.Manager
}

// NewHandler creates a new gold handler.
func NewHandler(
	repo *Repository,
	fxRepo *fxrate.Repository,
	goldPriceRepo *goldprice.Repository,
	jwtMgr *jwt.Manager,
) *Handler {
	return &Handler{
		repo:          repo,
		fxRepo:        fxRepo,
		goldPriceRepo: goldPriceRepo,
		jwtMgr:        jwtMgr,
	}
}

// RegisterRoutes registers gold routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/gold")
	protected := group.Use(middleware.AuthMiddleware(h.jwtMgr))
	protected.Get("/", h.list)
	protected.Post("/", h.create)
	protected.Get("/tax", h.listTaxPreferences)
	protected.Post("/tax", h.createTaxPreference)
	protected.Put("/tax/:id", h.updateTaxPreference)
	protected.Delete("/tax/:id", h.deleteTaxPreference)
	protected.Get("/:id", h.get)
	protected.Put("/:id", h.update)
	protected.Delete("/:id", h.delete)
}

// @id                   ListGolds
// @tags                 gold
// @accept               json
// @produce              json
// @param								Authorization header string true "Bearer token"
// @success              200 {object} ListGoldsRes
// @router               /api/gold [get]
func (h *Handler) list(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{
			Code:    fiber.StatusUnauthorized,
			Message: "unauthorized",
		})
	}
	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	fxRate, err := h.fxRepo.GetLatest()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	goldPrice, err := h.goldPriceRepo.GetLatest()
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	goldPriceIDRPerGram := (goldPrice.PricePerOunceUSD / 31.1035) * float64(fxRate.Rate)

	tax, err := h.repo.GetTaxPreferencesByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	mappedTax := make(map[float64]float64)
	for _, t := range tax {
		mappedTax[t.Carat] = t.TaxRate
	}

	golds, err := h.repo.GetGoldsByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	res := ListGoldsRes{Golds: make([]dto.Gold, 0, len(golds))}
	for _, g := range golds {
		sellPrice := g.Grams * goldPriceIDRPerGram * (g.Carat / 24.0) * (1 - mappedTax[g.Carat]/100)
		res.Golds = append(res.Golds, dto.Gold{
			Data:      g,
			SellPrice: sellPrice,
			Profit:    sellPrice - float64(g.Price),
		})
	}

	return c.Status(fiber.StatusOK).JSON(res)
}

// @id                   CreateGold
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                body body CreateGoldReq true "body"
// @success              201 {object} CreateGoldRes
// @router               /api/gold [post]
func (h *Handler) create(c *fiber.Ctx) error {
	var req CreateGoldReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}
	if !isValidGoldType(req.Type) {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid gold type"})
	}
	if req.Carat <= 0 || req.Carat > 24 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "carat must be between 0 and 24"})
	}

	parsedDate, err := time.Parse("2006-01-02", req.Date)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: "invalid date format, expected YYYY-MM-DD",
		})
	}

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	// verify wallet ownership
	ownerID, err := h.repo.GetWalletOwnerID(req.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	created, err := h.repo.CreateGold(&domain.Gold{
		WalletID: req.WalletID,
		Date:     parsedDate,
		Grams:    req.Grams,
		Price:    req.Price,
		Type:     req.Type,
		Carat:    req.Carat,
		Notes:    req.Notes,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateGoldRes{dto.Gold{Data: *created}})
}

// @id                   ListGoldTaxPreferences
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @success              200 {object} ListGoldTaxPreferencesRes
// @router               /api/gold/tax [get]
func (h *Handler) listTaxPreferences(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	prefs, err := h.repo.GetTaxPreferencesByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	res := ListGoldTaxPreferencesRes{Preferences: make([]dto.GoldTaxPreference, 0, len(prefs))}
	for i := range prefs {
		res.Preferences = append(res.Preferences, dto.GoldTaxPreference{
			ID:        prefs[i].ID,
			UserID:    prefs[i].UserID,
			Carat:     prefs[i].Carat,
			TaxRate:   prefs[i].TaxRate,
			CreatedAt: prefs[i].CreatedAt.Format(time.RFC3339),
			UpdatedAt: prefs[i].UpdatedAt.Format(time.RFC3339),
		})
	}

	return c.Status(fiber.StatusOK).JSON(res)
}

// @id                   CreateGoldTaxPreference
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                body body GoldTaxPreferenceReq true "body"
// @success              201 {object} CreateGoldTaxPreferenceRes
// @router               /api/gold/tax [post]
func (h *Handler) createTaxPreference(c *fiber.Ctx) error {
	var req GoldTaxPreferenceReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	if req.Carat <= 0 || req.Carat > 24 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "carat must be between 0 and 24"})
	}
	if req.TaxRate < 0 || req.TaxRate > 100 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "tax rate must be between 0 and 100"})
	}

	existing, err := h.repo.GetTaxPreferencesByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	for _, preference := range existing {
		if preference.Carat == req.Carat {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "duplicate carat preference"})
		}
	}

	created, err := h.repo.CreateTaxPreference(uint(parsedUserID), &domain.GoldTaxPreference{
		Carat:   req.Carat,
		TaxRate: req.TaxRate,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateGoldTaxPreferenceRes{Preference: dto.GoldTaxPreference{
		ID:        created.ID,
		UserID:    created.UserID,
		Carat:     created.Carat,
		TaxRate:   created.TaxRate,
		CreatedAt: created.CreatedAt.Format(time.RFC3339),
		UpdatedAt: created.UpdatedAt.Format(time.RFC3339),
	}})
}

// @id                   UpdateGoldTaxPreference
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "tax preference id"
// @param                body body GoldTaxPreferenceReq true "body"
// @success              200 {object} UpdateGoldTaxPreferenceRes
// @router               /api/gold/tax/{id} [put]
func (h *Handler) updateTaxPreference(c *fiber.Ctx) error {
	var req GoldTaxPreferenceReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	preferenceID, err := strconv.ParseUint(c.Params("id"), 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	if req.Carat <= 0 || req.Carat > 24 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "carat must be between 0 and 24"})
	}
	if req.TaxRate < 0 || req.TaxRate > 100 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "tax rate must be between 0 and 100"})
	}

	existing, err := h.repo.GetTaxPreferencesByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	for _, preference := range existing {
		if preference.ID != uint(preferenceID) && preference.Carat == req.Carat {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "duplicate carat preference"})
		}
	}

	updated, err := h.repo.UpdateTaxPreference(uint(parsedUserID), &domain.GoldTaxPreference{
		ID:      uint(preferenceID),
		Carat:   req.Carat,
		TaxRate: req.TaxRate,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(UpdateGoldTaxPreferenceRes{Preference: dto.GoldTaxPreference{
		ID:        updated.ID,
		UserID:    updated.UserID,
		Carat:     updated.Carat,
		TaxRate:   updated.TaxRate,
		CreatedAt: updated.CreatedAt.Format(time.RFC3339),
		UpdatedAt: updated.UpdatedAt.Format(time.RFC3339),
	}})
}

// @id                   DeleteGoldTaxPreference
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "tax preference id"
// @success              200 {object} DeleteGoldTaxPreferenceRes
// @router               /api/gold/tax/{id} [delete]
func (h *Handler) deleteTaxPreference(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	preferenceID, err := strconv.ParseUint(c.Params("id"), 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	if err := h.repo.DeleteTaxPreference(uint(parsedUserID), uint(preferenceID)); err != nil {
		if err == gorm.ErrRecordNotFound {
			return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: "tax preference not found"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(DeleteGoldTaxPreferenceRes{Message: "tax preference deleted"})
}

// @id                   GetGold
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "gold id"
// @success              200 {object} GetGoldRes
// @router               /api/gold/{id} [get]
func (h *Handler) get(c *fiber.Ctx) error {
	id := c.Params("id")
	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}
	g, err := h.repo.GetGoldByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	ownerID, err := h.repo.GetWalletOwnerID(g.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	return c.Status(fiber.StatusOK).JSON(GetGoldRes{dto.Gold{Data: *g}})
}

// @id                   UpdateGold
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "gold id"
// @param                body body UpdateGoldReq true "body"
// @success              200 {object} UpdateGoldRes
// @router               /api/gold/{id} [put]
func (h *Handler) update(c *fiber.Ctx) error {
	id := c.Params("id")
	var req UpdateGoldReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	g, err := h.repo.GetGoldByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	ownerID, err := h.repo.GetWalletOwnerID(g.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if req.Date != nil {
		parsedDate, err := time.Parse("2006-01-02", *req.Date)
		if err != nil {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
				Code:    fiber.StatusBadRequest,
				Message: "invalid date format, expected YYYY-MM-DD",
			})
		}
		g.Date = parsedDate
	}
	if req.Grams != nil {
		g.Grams = *req.Grams
	}
	if req.Price != nil {
		g.Price = *req.Price
	}
	if req.Type != nil {
		if !isValidGoldType(*req.Type) {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid gold type"})
		}
		g.Type = *req.Type
	}
	if req.Carat != nil {
		if *req.Carat <= 0 || *req.Carat > 24 {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "carat must be between 0 and 24"})
		}
		g.Carat = *req.Carat
	}
	if req.Notes != nil {
		g.Notes = *req.Notes
	}

	updated, err := h.repo.UpdateGold(uint(parsedID), g)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(UpdateGoldRes{dto.Gold{Data: *updated}})
}

// @id                   DeleteGold
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "gold id"
// @success              200 {object} DeleteGoldRes
// @router               /api/gold/{id} [delete]
func (h *Handler) delete(c *fiber.Ctx) error {
	id := c.Params("id")
	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	g, err := h.repo.GetGoldByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}
	userID := middleware.GetUserID(c)
	ownerID, err := h.repo.GetWalletOwnerID(g.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if err := h.repo.DeleteGold(uint(parsedID)); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(DeleteGoldRes{Message: "gold deleted"})
}

package gold

import (
	"strconv"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

func isValidGoldType(value string) bool {
	return value == domain.GoldTypePure || value == domain.GoldTypeJewelry
}

// Handler exposes gold HTTP endpoints.
type Handler struct {
	repo    *Repository
	jwtMgr  *jwt.Manager
	checker middleware.TokenStatusChecker
}

// NewHandler creates a new gold handler.
func NewHandler(repo *Repository, jwtMgr *jwt.Manager, checker middleware.TokenStatusChecker) *Handler {
	return &Handler{repo: repo, jwtMgr: jwtMgr, checker: checker}
}

// RegisterRoutes registers gold routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/gold")
	protected := group.Use(middleware.AuthMiddleware(h.jwtMgr, h.checker))
	protected.Get("/", h.list)
	protected.Post("/", h.create)
	protected.Get("/summary", h.summary)
	protected.Get("/:id", h.get)
	protected.Put("/:id", h.update)
	protected.Delete("/:id", h.delete)
}

// @id                   ListGolds
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @success              200 {object} ListGoldsRes
// @router               /api/gold [get]
func (h *Handler) list(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}
	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	golds, err := h.repo.GetGoldsByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	res := ListGoldsRes{Golds: make([]dto.Gold, 0, len(golds))}
	for _, g := range golds {
		res.Golds = append(res.Golds, dto.Gold{Data: g})
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
		WalletID:      req.WalletID,
		Date:          req.Date,
		Grams:         req.Grams,
		Price:         req.Price,
		Type:          req.Type,
		PurityPercent: req.PurityPercent,
		Notes:         req.Notes,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateGoldRes{dto.Gold{Data: *created}})
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
		g.Date = *req.Date
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
	if req.PurityPercent != nil {
		g.PurityPercent = *req.PurityPercent
	}
	if req.Notes != nil {
		g.Notes = *req.Notes
	}

	updated, err := h.repo.UpdateGold(g)
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

// @id                   GoldSummary
// @tags                 gold
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @success              200 {object} GoldSummaryRes
// @router               /api/gold/summary [get]
func (h *Handler) summary(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	totalGrams, totalValue, byType, err := h.repo.GetSummary(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(GoldSummaryRes{dto.GoldSummary{TotalGrams: totalGrams, TotalValue: totalValue, ByType: byType}})
}

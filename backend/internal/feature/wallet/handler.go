package wallet

import (
	"strconv"
	"strings"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

// Handler exposes wallet HTTP endpoints.
type Handler struct {
	repo       *Repository
	jwtManager *jwt.Manager
}

// NewHandler creates a new wallet handler.
func NewHandler(
	repo *Repository,
	jwtMgr *jwt.Manager,
) *Handler {
	return &Handler{
		repo:       repo,
		jwtManager: jwtMgr,
	}
}

// RegisterRoutes registers wallet routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/wallet")
	protected := group.Use(middleware.AuthMiddleware(h.jwtManager))
	protected.Get("/", h.list)
	protected.Post("/", h.create)
	protected.Get("/:id", h.get)
	protected.Put("/:id", h.update)
	protected.Delete("/:id", h.delete)
}

// @ID ListWallets
// @Tags wallet
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Success 200 {object} ListWalletsRes
// @Failure 401 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/wallet [get]
func (h *Handler) list(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	wallets, err := h.repo.GetWalletsByUserID(uint(parsedUserID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	res := ListWalletsRes{Wallets: make([]dto.Wallet, 0, len(wallets))}
	for _, w := range wallets {
		res.Wallets = append(res.Wallets, dto.Wallet{Data: w})
	}

	return c.Status(fiber.StatusOK).JSON(res)
}

// @ID CreateWallet
// @Tags wallet
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Param Idempotency-Key header string false "Unique key per submission attempt for safe retry (UUID recommended)"
// @Param body body CreateWalletReq true "body"
// @Success 201 {object} CreateWalletRes
// @Failure 400 {object} dto.Error
// @Failure 401 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/wallet [post]
func (h *Handler) create(c *fiber.Ctx) error {
	var req CreateWalletReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	if req.Name == "" {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "name is required"})
	}
	if req.Type == "" {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "type is required"})
	}

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	idempotencyKey := strings.TrimSpace(c.Get("Idempotency-Key"))
	if len(idempotencyKey) > 64 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "Idempotency-Key must be at most 64 characters"})
	}

	created, err := h.repo.CreateWallet(&domain.Wallet{
		UserID: uint(parsedUserID),
		Name:   req.Name,
		Type:   req.Type,
	}, idempotencyKey)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateWalletRes{dto.Wallet{Data: *created}})
}

// @ID GetWallet
// @Tags wallet
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Param id path int true "Wallet ID"
// @Success 200 {object} GetWalletRes
// @Failure 400 {object} dto.Error
// @Failure 401 {object} dto.Error
// @Failure 404 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/wallet/{id} [get]
func (h *Handler) get(c *fiber.Ctx) error {
	id := c.Params("id")
	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}
	wallet, err := h.repo.GetWalletByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if strconv.FormatUint(uint64(wallet.UserID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	return c.Status(fiber.StatusOK).JSON(GetWalletRes{dto.Wallet{Data: *wallet}})
}

// @ID UpdateWallet
// @Tags wallet
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Param id path int true "Wallet ID"
// @Param body body UpdateWalletReq true "body"
// @Success 200 {object} UpdateWalletRes
// @Failure 400 {object} dto.Error
// @Failure 401 {object} dto.Error
// @Failure 404 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/wallet/{id} [put]
func (h *Handler) update(c *fiber.Ctx) error {
	id := c.Params("id")
	var req UpdateWalletReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	wallet, err := h.repo.GetWalletByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if strconv.FormatUint(uint64(wallet.UserID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if req.Name != nil {
		wallet.Name = *req.Name
	}
	if req.Type != nil {
		wallet.Type = *req.Type
	}

	updated, err := h.repo.UpdateWallet(wallet)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(UpdateWalletRes{dto.Wallet{Data: *updated}})
}

// @ID DeleteWallet
// @Tags wallet
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Param id path int true "Wallet ID"
// @Success 200 {object} DeleteWalletRes
// @Failure 400 {object} dto.Error
// @Failure 401 {object} dto.Error
// @Failure 404 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/wallet/{id} [delete]
func (h *Handler) delete(c *fiber.Ctx) error {
	id := c.Params("id")
	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}
	wallet, err := h.repo.GetWalletByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if strconv.FormatUint(uint64(wallet.UserID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if err := h.repo.DeleteWallet(uint(parsedID)); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(DeleteWalletRes{Message: "wallet deleted"})
}

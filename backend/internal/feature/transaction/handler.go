package transaction

import (
	"strconv"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

// Handler exposes transaction HTTP endpoints.
type Handler struct {
	repo    *Repository
	jwtMgr  *jwt.Manager
	checker middleware.TokenStatusChecker
}

// NewHandler creates a new transaction handler.
func NewHandler(repo *Repository, jwtMgr *jwt.Manager, checker middleware.TokenStatusChecker) *Handler {
	return &Handler{repo: repo, jwtMgr: jwtMgr, checker: checker}
}

// RegisterRoutes registers transaction routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/transaction")
	protected := group.Use(middleware.AuthMiddleware(h.jwtMgr, h.checker))
	protected.Get("/", h.list)
	protected.Post("/", h.create)
	protected.Get("/:id", h.get)
	protected.Put("/:id", h.update)
	protected.Delete("/:id", h.delete)
}

// list requires query param `wallet_id` to list transactions for a wallet.
func (h *Handler) list(c *fiber.Ctx) error {
	walletID := c.Query("wallet_id")
	if walletID == "" {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "wallet_id is required"})
	}

	// verify ownership
	parsedWalletID, err := strconv.ParseUint(walletID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	ownerID, err := h.repo.GetWalletOwnerID(uint(parsedWalletID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	txs, err := h.repo.GetTransactionsByWalletID(uint(parsedWalletID))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	res := ListTransactionsRes{Transactions: make([]dto.Transaction, 0, len(txs))}
	for _, t := range txs {
		res.Transactions = append(res.Transactions, dto.Transaction{Data: t})
	}

	return c.Status(fiber.StatusOK).JSON(res)
}

func (h *Handler) create(c *fiber.Ctx) error {
	var req CreateTransactionReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	ownerID, err := h.repo.GetWalletOwnerID(req.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	// minimal validation
	if req.Type == "" || req.Amount <= 0 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid type or amount"})
	}

	created, err := h.repo.CreateTransaction(&domain.Transaction{
		WalletID:    req.WalletID,
		Type:        req.Type,
		Amount:      req.Amount,
		CategoryID:  req.CategoryID,
		Description: req.Description,
		Date:        req.Date,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateTransactionRes{dto.Transaction{Data: *created}})
}

func (h *Handler) get(c *fiber.Ctx) error {
	id := c.Params("id")
	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}
	tx, err := h.repo.GetTransactionByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	ownerID, err := h.repo.GetWalletOwnerID(tx.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	return c.Status(fiber.StatusOK).JSON(GetTransactionRes{dto.Transaction{Data: *tx}})
}

func (h *Handler) update(c *fiber.Ctx) error {
	id := c.Params("id")
	var req UpdateTransactionReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	tx, err := h.repo.GetTransactionByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	ownerID, err := h.repo.GetWalletOwnerID(tx.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if req.WalletID != nil {
		tx.WalletID = *req.WalletID
	}
	if req.Type != nil {
		tx.Type = *req.Type
	}
	if req.Amount != nil {
		tx.Amount = *req.Amount
	}
	if req.CategoryID != nil {
		tx.CategoryID = *req.CategoryID
	}
	if req.Description != nil {
		tx.Description = *req.Description
	}
	if req.Date != nil {
		tx.Date = *req.Date
	}

	updated, err := h.repo.UpdateTransaction(tx)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(UpdateTransactionRes{dto.Transaction{Data: *updated}})
}

func (h *Handler) delete(c *fiber.Ctx) error {
	id := c.Params("id")
	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}
	tx, err := h.repo.GetTransactionByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	ownerID, err := h.repo.GetWalletOwnerID(tx.WalletID)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if strconv.FormatUint(uint64(ownerID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if err := h.repo.DeleteTransaction(uint(parsedID)); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(DeleteTransactionRes{Message: "transaction deleted"})
}

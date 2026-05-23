package transaction

import (
	"strconv"
	"time"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	categoryfeature "github.com/artafinance/backend/internal/feature/category"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

// Handler exposes transaction HTTP endpoints.
type Handler struct {
	repo         *Repository
	categoryRepo *categoryfeature.Repository
	jwtMgr       *jwt.Manager
	checker      middleware.TokenStatusChecker
}

// NewHandler creates a new transaction handler.
func NewHandler(repo *Repository, categoryRepo *categoryfeature.Repository, jwtMgr *jwt.Manager, checker middleware.TokenStatusChecker) *Handler {
	return &Handler{repo: repo, categoryRepo: categoryRepo, jwtMgr: jwtMgr, checker: checker}
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

// @id                   ListTransactions
// @tags                 transaction
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                wallet_id query int true "wallet id"
// @success              200 {object} ListTransactionsRes
// @failure              400 {object} dto.Error
// @failure              401 {object} dto.Error
// @failure              500 {object} dto.Error
// @router               /api/transaction [get]
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

// @id                   CreateTransaction
// @tags                 transaction
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                body body CreateTransactionReq true "body"
// @success              201 {object} CreateTransactionRes
// @failure              400 {object} dto.Error
// @failure              401 {object} dto.Error
// @failure              500 {object} dto.Error
// @router               /api/transaction [post]
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

	if req.CategoryID == 0 || req.Amount <= 0 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid category or amount"})
	}

	category, err := h.categoryRepo.GetCategoryByID(req.CategoryID)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid category"})
	}
	if category.UserID != nil && strconv.FormatUint(uint64(*category.UserID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedDate, err := time.Parse("2006-01-02", req.Date)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid date format"})
	}

	created, err := h.repo.CreateTransaction(&domain.Transaction{
		WalletID:    req.WalletID,
		Amount:      req.Amount,
		CategoryID:  req.CategoryID,
		Description: req.Description,
		Date:        parsedDate,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateTransactionRes{dto.Transaction{Data: *created}})
}

// @id                   GetTransaction
// @tags                 transaction
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "transaction id"
// @success              200 {object} GetTransactionRes
// @failure              400 {object} dto.Error
// @failure              401 {object} dto.Error
// @failure              404 {object} dto.Error
// @failure              500 {object} dto.Error
// @router               /api/transaction/{id} [get]
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

// @id                   UpdateTransaction
// @tags                 transaction
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "transaction id"
// @param                body body UpdateTransactionReq true "body"
// @success              200 {object} UpdateTransactionRes
// @failure              400 {object} dto.Error
// @failure              401 {object} dto.Error
// @failure              404 {object} dto.Error
// @failure              500 {object} dto.Error
// @router               /api/transaction/{id} [put]
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
	if req.Amount != nil {
		tx.Amount = *req.Amount
	}
	if req.CategoryID != nil {
		category, err := h.categoryRepo.GetCategoryByID(*req.CategoryID)
		if err != nil {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid category"})
		}
		if category.UserID != nil && strconv.FormatUint(uint64(*category.UserID), 10) != userID {
			return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
		}
		tx.CategoryID = *req.CategoryID
	}
	if req.Description != nil {
		tx.Description = *req.Description
	}
	if req.Date != nil {
		parsedDate, err := time.Parse("2006-01-02", *req.Date)
		if err != nil {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid date format"})
		}
		tx.Date = parsedDate
	}

	updated, err := h.repo.UpdateTransaction(tx)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(UpdateTransactionRes{dto.Transaction{Data: *updated}})
}

// @id                   DeleteTransaction
// @tags                 transaction
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "transaction id"
// @success              200 {object} DeleteTransactionRes
// @failure              400 {object} dto.Error
// @failure              401 {object} dto.Error
// @failure              404 {object} dto.Error
// @failure              500 {object} dto.Error
// @router               /api/transaction/{id} [delete]
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

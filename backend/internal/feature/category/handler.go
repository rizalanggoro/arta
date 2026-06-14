package category

import (
	"strconv"
	"strings"
	"time"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

// Handler exposes category HTTP endpoints.
type Handler struct {
	repo   *Repository
	jwtMgr *jwt.Manager
}

// NewHandler creates a new category handler.
func NewHandler(repo *Repository, jwtMgr *jwt.Manager) *Handler {
	return &Handler{repo: repo, jwtMgr: jwtMgr}
}

// RegisterRoutes registers category routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/category")
	protected := group.Use(middleware.AuthMiddleware(h.jwtMgr))
	protected.Get("/", h.list)
	protected.Post("/", h.create)
	protected.Get("/:category_id", h.get)
	protected.Put("/:id", h.update)
	protected.Delete("/:id", h.delete)
}

// @id                   ListCategories
// @tags                 category
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @success              200 {object} ListCategoriesRes
// @router               /api/category [get]
func (h *Handler) list(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	categoryType := strings.TrimSpace(strings.ToLower(c.Query("type")))
	if categoryType != "" && !isAllowedCategoryType(categoryType) {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "type must be income or expense"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	cats, err := h.repo.GetCategoriesByUserID(uint(parsedUserID), categoryType)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	res := ListCategoriesRes{Categories: make([]dto.Category, 0, len(cats))}
	for _, v := range cats {
		res.Categories = append(res.Categories, dto.Category{Data: v})
	}

	return c.Status(fiber.StatusOK).JSON(res)
}

// @id                   CreateCategory
// @tags                 category
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                body body CreateCategoryReq true "body"
// @success              201 {object} CreateCategoryRes
// @router               /api/category [post]
func (h *Handler) create(c *fiber.Ctx) error {
	var req CreateCategoryReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	if req.Name == "" || req.Type == "" {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "name and type are required"})
	}
	if !isAllowedCategoryType(req.Type) {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "type must be income or expense"})
	}

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	created, err := h.repo.CreateCategory(&domain.Category{
		UserID: func() *uint { u := uint(parsedUserID); return &u }(),
		Name:   req.Name,
		Type:   req.Type,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateCategoryRes{dto.Category{Data: *created}})
}

// @id          GetCategory
// @tags        category
// @accept      json
// @produce     json
// @param       Authorization header string true "Bearer token"
// @param       category_id path int true "category id"
// @param 			wallet_id query int false "wallet_id"
// @param 			include_total_amount query bool false "include_total_amount"
// @param 			include_transactions query bool false "include_transactions"
// @param 			start_date query string false "start_date"
// @param 			end_date query string false "end_date"
// @success     200 {object} dto.Category
// @router      /api/category/{category_id} [get]
func (h *Handler) get(c *fiber.Ctx) error {
	userId, err := strconv.Atoi(middleware.GetUserID(c))
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	categoryId, err := c.ParamsInt("category_id")
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
			Code:    fiber.StatusBadRequest,
			Message: err.Error(),
		})
	}

	walletId := c.QueryInt("wallet_id", 0)
	includeTotalAmount := c.QueryBool("include_total_amount", false)
	includeTransactions := c.QueryBool("include_transactions", false)
	startDateStr := c.Query("start_date")
	endDateStr := c.Query("end_date")

	var startDate, endDate time.Time
	if startDateStr != "" {
		startDate, err = time.Parse("2006-01-02", startDateStr)
		if err != nil {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
				Code:    fiber.StatusBadRequest,
				Message: err.Error(),
			})
		}
	}
	if endDateStr != "" {
		endDate, err = time.Parse("2006-01-02", endDateStr)
		if err != nil {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{
				Code:    fiber.StatusBadRequest,
				Message: err.Error(),
			})
		}
	}

	res, err := h.repo.Get(GetCategoryFilterFilter{
		CategoryId:          uint(categoryId),
		UserId:              uint(userId),
		WalletId:            uint(walletId),
		IncludeTotalAmount:  includeTotalAmount,
		IncludeTransactions: includeTransactions,
		StartDate:           startDate,
		EndDate:             endDate,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{
			Code:    fiber.StatusInternalServerError,
			Message: err.Error(),
		})
	}

	return c.Status(fiber.StatusOK).JSON(res)
}

// @id                   UpdateCategory
// @tags                 category
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "category id"
// @param                body body UpdateCategoryReq true "body"
// @success              200 {object} UpdateCategoryRes
// @router               /api/category/{id} [put]
func (h *Handler) update(c *fiber.Ctx) error {
	id := c.Params("id")
	var req UpdateCategoryReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	cat, err := h.repo.GetCategoryByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if cat.UserID == nil || strconv.FormatUint(uint64(*cat.UserID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if req.Name != nil {
		cat.Name = *req.Name
	}
	if req.Type != nil {
		if !isAllowedCategoryType(*req.Type) {
			return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "type must be income or expense"})
		}
		cat.Type = *req.Type
	}

	updated, err := h.repo.UpdateCategory(cat)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(UpdateCategoryRes{dto.Category{Data: *updated}})
}

// @id                   DeleteCategory
// @tags                 category
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "category id"
// @success              200 {object} DeleteCategoryRes
// @router               /api/category/{id} [delete]
func (h *Handler) delete(c *fiber.Ctx) error {
	id := c.Params("id")
	parsedID, err := strconv.ParseUint(id, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}
	cat, err := h.repo.GetCategoryByID(uint(parsedID))
	if err != nil {
		return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: err.Error()})
	}

	userID := middleware.GetUserID(c)
	if cat.UserID == nil || strconv.FormatUint(uint64(*cat.UserID), 10) != userID {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if err := h.repo.DeleteCategory(uint(parsedID)); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(DeleteCategoryRes{Message: "category deleted"})
}

func isAllowedCategoryType(value string) bool {
	switch strings.ToLower(value) {
	case "income", "expense":
		return true
	default:
		return false
	}
}

package category

import (
	"strconv"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/gofiber/fiber/v2"
)

// Handler exposes category HTTP endpoints.
type Handler struct {
	repo    *Repository
	jwtMgr  *jwt.Manager
	checker middleware.TokenStatusChecker
}

// NewHandler creates a new category handler.
func NewHandler(repo *Repository, jwtMgr *jwt.Manager, checker middleware.TokenStatusChecker) *Handler {
	return &Handler{repo: repo, jwtMgr: jwtMgr, checker: checker}
}

// RegisterRoutes registers category routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/category")
	protected := group.Use(middleware.AuthMiddleware(h.jwtMgr, h.checker))
	protected.Get("/", h.list)
	protected.Post("/", h.create)
	protected.Get("/:id", h.get)
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

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	cats, err := h.repo.GetCategoriesByUserID(uint(parsedUserID))
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

	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedUserID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	created, err := h.repo.CreateCategory(&domain.Category{
		UserID:    func() *uint { u := uint(parsedUserID); return &u }(),
		Name:      req.Name,
		Type:      req.Type,
		Icon:      req.Icon,
		Color:     req.Color,
		IsCustom:  true,
		IsDefault: false,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(CreateCategoryRes{dto.Category{Data: *created}})
}

// @id                   GetCategory
// @tags                 category
// @accept               json
// @produce              json
// @param                Authorization header string true "Bearer token"
// @param                id path int true "category id"
// @success              200 {object} GetCategoryRes
// @router               /api/category/{id} [get]
func (h *Handler) get(c *fiber.Ctx) error {
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
	// allow access if category is default (UserID nil) or owned by user
	if cat.UserID != nil {
		if strconv.FormatUint(uint64(*cat.UserID), 10) != userID {
			return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
		}
	}

	return c.Status(fiber.StatusOK).JSON(GetCategoryRes{dto.Category{Data: *cat}})
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
		cat.Type = *req.Type
	}
	if req.Icon != nil {
		cat.Icon = *req.Icon
	}
	if req.Color != nil {
		cat.Color = *req.Color
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

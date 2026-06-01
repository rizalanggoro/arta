package auth

import (
	"errors"
	"strconv"
	"strings"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/artafinance/backend/pkg/middleware"
	"github.com/artafinance/backend/pkg/util"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

// Handler exposes auth HTTP endpoints.
type Handler struct {
	repo       *Repository
	jwtManager *jwt.Manager
}

// NewHandler creates a new auth handler.
func NewHandler(repo *Repository, jwtManager *jwt.Manager) *Handler {
	return &Handler{
		repo:       repo,
		jwtManager: jwtManager,
	}
}

// RegisterRoutes registers auth routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/auth")
	group.Post("/register", h.register)
	group.Post("/login", h.login)

	protected := group.Use(middleware.AuthMiddleware(h.jwtManager, h.repo))
	protected.Get("/me", h.me)
	protected.Post("/logout", h.logout)
}

// @Summary Register a new user
// @Description Create a new account and issue the first session token.
// @Tags auth
// @Accept json
// @Produce json
// @Param body body RegisterReq true "body"
// @Success 201 {object} RegisterRes
// @Failure 400 {object} dto.Error
// @Failure 409 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Router /api/auth/register [post]
func (h *Handler) register(c *fiber.Ctx) error {
	var req RegisterReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	req.Email = strings.ToLower(strings.TrimSpace(req.Email))
	req.Name = strings.TrimSpace(req.Name)

	if !util.ValidateEmail(req.Email) {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid email"})
	}
	if req.Name == "" {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "name is required"})
	}
	if !util.ValidatePassword(req.Password) {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "password must be at least 8 characters"})
	}

	if existingUser, err := h.repo.GetUserByEmail(req.Email); err == nil && existingUser != nil {
		return c.Status(fiber.StatusConflict).JSON(dto.Error{Code: fiber.StatusConflict, Message: "email already registered"})
	} else if err != nil && !errors.Is(err, gorm.ErrRecordNotFound) {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	hashedPassword, err := util.HashPassword(req.Password)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	createdUser, err := h.repo.CreateUser(&domain.User{
		Email:    req.Email,
		Name:     req.Name,
		Password: hashedPassword,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	token, _, err := h.jwtManager.GenerateToken(strconv.FormatUint(uint64(createdUser.ID), 10), createdUser.Email)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	if _, err := h.repo.CreateSession(&domain.Session{
		UserID: createdUser.ID,
		Token:  token,
	}); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(RegisterRes{
		UserID: strconv.FormatUint(uint64(createdUser.ID), 10),
		Email:  createdUser.Email,
		Name:   createdUser.Name,
		Token:  token,
	})
}

// @Summary Login a user
// @Description Validate credentials and issue a session token.
// @Tags auth
// @Accept json
// @Produce json
// @Param body body LoginReq true "body"
// @Success 200 {object} LoginRes
// @Failure 400 {object} dto.Error
// @Failure 401 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Router /api/auth/login [post]
func (h *Handler) login(c *fiber.Ctx) error {
	var req LoginReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	req.Email = strings.ToLower(strings.TrimSpace(req.Email))
	if !util.ValidateEmail(req.Email) {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "invalid email"})
	}
	if req.Password == "" {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "password is required"})
	}

	user, err := h.repo.GetUserByEmail(req.Email)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "invalid credentials"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}
	if user == nil {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "invalid credentials"})
	}
	if !util.VerifyPassword(user.Password, req.Password) {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "invalid credentials"})
	}

	token, _, err := h.jwtManager.GenerateToken(strconv.FormatUint(uint64(user.ID), 10), user.Email)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	if _, err := h.repo.CreateSession(&domain.Session{
		UserID: user.ID,
		Token:  token,
	}); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(LoginRes{
		UserID: strconv.FormatUint(uint64(user.ID), 10),
		Email:  user.Email,
		Name:   user.Name,
		Token:  token,
	})
}

// @Summary Get current user
// @Description Return the authenticated user's profile.
// @Tags auth
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Success 200 {object} MeRes
// @Failure 401 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/auth/me [get]
func (h *Handler) me(c *fiber.Ctx) error {
	userID := middleware.GetUserID(c)
	if userID == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	parsedID, err := strconv.ParseUint(userID, 10, 64)
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	user, err := h.repo.GetUserByID(uint(parsedID))
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(MeRes{
		User: dto.User{
			Data: *user,
		},
		UpdatedAt: user.UpdatedAt,
	})
}

// @id logout
// @Summary Logout current session
// @Description Delete the current session token.
// @Tags auth
// @Accept json
// @Produce json
// @Param Authorization header string true "Bearer token"
// @Success 200 {object} LogoutRes
// @Failure 401 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Security Bearer
// @Router /api/auth/logout [post]
func (h *Handler) logout(c *fiber.Ctx) error {
	token := middleware.GetToken(c)
	if token == "" {
		return c.Status(fiber.StatusUnauthorized).JSON(dto.Error{Code: fiber.StatusUnauthorized, Message: "unauthorized"})
	}

	if err := h.repo.DeleteSessionByToken(token); err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(LogoutRes{Message: "logged out successfully"})
}

package release

import (
	"net/url"
	"strings"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

// Handler exposes release HTTP endpoints.
type Handler struct {
	repo *Repository
}

// NewHandler creates a new release handler.
func NewHandler(repo *Repository) *Handler {
	return &Handler{repo: repo}
}

// RegisterRoutes registers release routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	group := router.Group("/release")
	group.Post("", h.create)
	group.Get("/latest", h.latest)
}

// @Summary Create a new release
// @Description Store a new APK release url and its version code.
// @Tags release
// @Accept json
// @Produce json
// @Param body body CreateReleaseReq true "body"
// @Success 201 {object} ReleaseRes
// @Failure 400 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Router /api/release [post]
func (h *Handler) create(c *fiber.Ctx) error {
	var req CreateReleaseReq
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: err.Error()})
	}

	req.URL = strings.TrimSpace(req.URL)
	if req.URL == "" {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "url is required"})
	}
	parsedURL, err := url.ParseRequestURI(req.URL)
	if err != nil || (parsedURL.Scheme != "http" && parsedURL.Scheme != "https") {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "url must be a valid http or https url"})
	}
	if req.VersionCode <= 0 {
		return c.Status(fiber.StatusBadRequest).JSON(dto.Error{Code: fiber.StatusBadRequest, Message: "version_code must be greater than zero"})
	}

	created, err := h.repo.CreateRelease(&domain.Release{
		URL:         req.URL,
		VersionCode: req.VersionCode,
	})
	if err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusCreated).JSON(ReleaseRes{
		Release: dto.Release{Data: *created},
	})
}

// @Summary Get latest release
// @Description Return the release with the highest version code.
// @Tags release
// @Accept json
// @Produce json
// @Success 200 {object} ReleaseRes
// @Failure 404 {object} dto.Error
// @Failure 500 {object} dto.Error
// @Router /api/release/latest [get]
func (h *Handler) latest(c *fiber.Ctx) error {
	release, err := h.repo.GetLatestRelease()
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return c.Status(fiber.StatusNotFound).JSON(dto.Error{Code: fiber.StatusNotFound, Message: "release not found"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(dto.Error{Code: fiber.StatusInternalServerError, Message: err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(ReleaseRes{
		dto.Release{Data: *release},
	})
}

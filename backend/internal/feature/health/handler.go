package health

import (
	"github.com/gofiber/fiber/v2"
)

// Handler exposes health check endpoint.
type Handler struct{}

// NewHandler creates a new health handler.
func NewHandler() *Handler {
	return &Handler{}
}

// RegisterRoutes registers health routes.
func (h *Handler) RegisterRoutes(router fiber.Router) {
	router.Get("/health", h.health)
}

// HealthRes represents health check response.
// @name HealthRes
type HealthRes struct {
	Status         string `json:"status"`
	AppVersion     string `json:"app_version"`
	AppVersionCode int    `json:"app_version_code"`
}

// @Summary Health check
// @Description Return server health status and app version.
// @Tags health
// @Produce json
// @Success 200 {object} HealthRes
// @Router /api/health [get]
func (h *Handler) health(c *fiber.Ctx) error {
	return c.JSON(HealthRes{
		Status:         "ok",
		AppVersion:     "1.0.18",
		AppVersionCode: 18,
	})
}

package middleware

import (
	"strings"

	"github.com/artafinance/backend/pkg/jwt"
	"github.com/gofiber/fiber/v2"
)

// AuthMiddleware is a Fiber middleware for JWT authentication
func AuthMiddleware(
	jwtManager *jwt.Manager,
	// checker TokenStatusChecker,
) fiber.Handler {
	return func(c *fiber.Ctx) error {
		// Get Authorization header
		authHeader := c.Get("Authorization")
		if authHeader == "" {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "missing authorization header",
			})
		}

		// Extract token from "Bearer <token>"
		parts := strings.Split(authHeader, " ")
		if len(parts) != 2 || parts[0] != "Bearer" {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "invalid authorization header format",
			})
		}

		tokenString := parts[1]

		// Validate token
		claims, err := jwtManager.ValidateToken(tokenString)
		if err != nil {
			return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
				"error": "invalid or expired token",
			})
		}

		// if checker != nil {
		// 	isActive, err := checker.IsTokenActive(tokenString)
		// 	if err != nil || !isActive {
		// 		return c.Status(fiber.StatusUnauthorized).JSON(fiber.Map{
		// 			"error": "token has been revoked",
		// 		})
		// 	}
		// }

		// Store claims in context for later use
		c.Locals("user_id", claims.UserID)
		c.Locals("email", claims.Email)
		c.Locals("claims", claims)
		c.Locals("token", tokenString)

		return c.Next()
	}
}

// GetUserID extracts user ID from context
func GetUserID(c *fiber.Ctx) string {
	userID, ok := c.Locals("user_id").(string)
	if !ok {
		return ""
	}
	return userID
}

// GetClaims extracts JWT claims from context
// func GetClaims(c *fiber.Ctx) *jwt.Claims {
// 	claims, ok := c.Locals("claims").(*jwt.Claims)
// 	if !ok {
// 		return nil
// 	}
// 	return claims
// }

// GetToken extracts the bearer token from context.
func GetToken(c *fiber.Ctx) string {
	token, ok := c.Locals("token").(string)
	if !ok {
		return ""
	}
	return token
}

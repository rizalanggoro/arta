package auth

import (
	"time"

	"github.com/artafinance/backend/internal/dto"
)

// RegisterRes represents register response payload.
type RegisterRes struct {
	UserID string `json:"user_id"`
	Email  string `json:"email"`
	Name   string `json:"name"`
	Token  string `json:"token"`
} // @name RegisterRes

// LoginRes represents login response payload.
type LoginRes struct {
	UserID string `json:"user_id"`
	Email  string `json:"email"`
	Name   string `json:"name"`
	Token  string `json:"token"`
} // @name LoginRes

// MeRes represents current user response payload.
type MeRes struct {
	dto.User
	UpdatedAt time.Time `json:"updated_at"`
} // @name MeRes

// LogoutRes represents logout response payload.
type LogoutRes struct {
	Message string `json:"message"`
} // @name LogoutRes

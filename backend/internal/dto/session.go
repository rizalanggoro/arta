package dto

import "github.com/artafinance/backend/internal/domain"

// Session represents session response DTO
// @name Session
type Session struct {
	Data domain.Session `json:"data"`
}

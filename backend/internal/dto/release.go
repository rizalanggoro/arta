package dto

import "github.com/artafinance/backend/internal/domain"

// Release represents release response DTO.
// @name Release
type Release struct {
	Data domain.Release `json:"data"`
}

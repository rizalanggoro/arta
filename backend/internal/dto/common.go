package dto

// Error represents a shared error response DTO.
// @name Error
type Error struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

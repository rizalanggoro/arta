package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Session represents a JWT session token in the domain layer
// @name Session
type Session struct {
	ID        uint      `json:"id"`
	UserID    uint      `json:"user_id"`
	Token     string    `json:"token"`
	CreatedAt time.Time `json:"created_at"`
}

// FromSessionModel maps a session model to the domain layer.
func FromSessionModel(m *model.Session) *Session {
	if m == nil {
		return nil
	}

	return &Session{
		ID:        m.ID,
		UserID:    m.UserID,
		Token:     m.Token,
		CreatedAt: m.CreatedAt,
	}
}

// ToModel maps the domain layer to a session model.
func (s *Session) ToModel() *model.Session {
	if s == nil {
		return nil
	}

	return &model.Session{
		Model:  gorm.Model{ID: s.ID},
		UserID: s.UserID,
		Token:  s.Token,
	}
}

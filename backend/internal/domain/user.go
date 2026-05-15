package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// User represents a user in the domain layer
// @name User
type User struct {
	ID        uint      `json:"id"`
	Email     string    `json:"email"`
	Name      string    `json:"name"`
	Password  string    `json:"-"`
	Currency  string    `json:"currency"`
	IsActive  bool      `json:"is_active"`
	Wallets   []Wallet  `json:"wallets,omitempty"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

// FromUserModel maps a user model to the domain layer.
func FromUserModel(m *model.User) *User {
	if m == nil {
		return nil
	}

	return &User{
		ID:        m.ID,
		Email:     m.Email,
		Name:      m.Name,
		Password:  m.Password,
		Currency:  m.Currency,
		IsActive:  m.IsActive,
		Wallets:   make([]Wallet, 0, len(m.Wallets)),
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a user model.
func (u *User) ToModel() *model.User {
	if u == nil {
		return nil
	}

	return &model.User{
		Model:    gorm.Model{ID: u.ID},
		Email:    u.Email,
		Name:     u.Name,
		Password: u.Password,
		Currency: u.Currency,
		IsActive: u.IsActive,
	}
}

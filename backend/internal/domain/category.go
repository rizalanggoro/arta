package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Category represents a transaction category in the domain layer
// @name Category
type Category struct {
	ID        uint      `json:"id"`
	UserID    *uint     `json:"user_id,omitempty"` // nil for default categories
	Name      string    `json:"name"`
	Type      string    `json:"type"` // income, expense, general
	Icon      string    `json:"icon"`
	Color     string    `json:"color"`
	IsCustom  bool      `json:"is_custom"`
	IsDefault bool      `json:"is_default"`
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

// FromCategoryModel maps a category model to the domain layer.
func FromCategoryModel(m *model.Category) *Category {
	if m == nil {
		return nil
	}

	var userID *uint
	if m.UserID != nil {
		value := *m.UserID
		userID = &value
	}

	return &Category{
		ID:        m.ID,
		UserID:    userID,
		Name:      m.Name,
		Type:      m.Type,
		Icon:      m.Icon,
		Color:     m.Color,
		IsCustom:  m.IsCustom,
		IsDefault: m.IsDefault,
		CreatedAt: m.CreatedAt,
		UpdatedAt: m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a category model.
func (c *Category) ToModel() *model.Category {
	if c == nil {
		return nil
	}

	var userID *uint
	if c.UserID != nil {
		value := *c.UserID
		userID = &value
	}

	return &model.Category{
		Model:     gorm.Model{ID: c.ID},
		UserID:    userID,
		Name:      c.Name,
		Type:      c.Type,
		Icon:      c.Icon,
		Color:     c.Color,
		IsCustom:  c.IsCustom,
		IsDefault: c.IsDefault,
	}
}

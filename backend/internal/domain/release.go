package domain

import (
	"time"

	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Release represents an application release in the domain layer.
// @name Release
type Release struct {
	ID          uint      `json:"id"`
	URL         string    `json:"url"`
	VersionCode int       `json:"version_code"`
	CreatedAt   time.Time `json:"created_at"`
	UpdatedAt   time.Time `json:"updated_at"`
}

// FromReleaseModel maps a release model to the domain layer.
func FromReleaseModel(m *model.Release) *Release {
	if m == nil {
		return nil
	}

	return &Release{
		ID:          m.ID,
		URL:         m.URL,
		VersionCode: m.VersionCode,
		CreatedAt:   m.CreatedAt,
		UpdatedAt:   m.UpdatedAt,
	}
}

// ToModel maps the domain layer to a release model.
func (r *Release) ToModel() *model.Release {
	if r == nil {
		return nil
	}

	return &model.Release{
		Model:       gorm.Model{ID: r.ID},
		URL:         r.URL,
		VersionCode: r.VersionCode,
	}
}

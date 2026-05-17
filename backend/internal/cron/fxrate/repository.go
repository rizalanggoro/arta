package fxrate

import (
	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository stores FX rate snapshots.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new FX rate repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// Create stores a new FX rate snapshot.
func (r *Repository) Create(rate *domain.FxRate) (*domain.FxRate, error) {
	m := rate.ToModel()
	if err := r.db.Create(m).Error; err != nil {
		return nil, err
	}
	return domain.FromFxRateModel(m), nil
}

// GetLatest returns the most recently stored snapshot.
func (r *Repository) GetLatest() (*domain.FxRate, error) {
	var m model.FxRate
	if err := r.db.Order("created_at desc").First(&m).Error; err != nil {
		return nil, err
	}
	return domain.FromFxRateModel(&m), nil
}

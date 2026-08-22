package fxrate

import (
	"fmt"
	"time"

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

// GetHistory returns FX rate snapshots stored within the given number of days, oldest first.
// Ranges shorter than a week return raw snapshots; longer ranges are downsampled to the last
// snapshot per hour (under a month) or per day.
func (r *Repository) GetHistory(days int) ([]*domain.FxRate, error) {
	var ms []model.FxRate
	since := time.Now().AddDate(0, 0, -days)

	var err error
	if days < 7 {
		err = r.db.
			Where("created_at >= ?", since).
			Order("created_at asc").
			Find(&ms).Error
	} else {
		bucket := "hour"
		if days >= 30 {
			bucket = "day"
		}
		err = r.db.Raw(fmt.Sprintf(`
			SELECT * FROM (
				SELECT DISTINCT ON (DATE_TRUNC('%s', created_at)) *
				FROM fx_rates
				WHERE created_at >= ?
				ORDER BY DATE_TRUNC('%s', created_at), created_at DESC
			) recent
			ORDER BY created_at ASC`, bucket, bucket), since).
			Scan(&ms).Error
	}
	if err != nil {
		return nil, err
	}

	rates := make([]*domain.FxRate, 0, len(ms))
	for i := range ms {
		rates = append(rates, domain.FromFxRateModel(&ms[i]))
	}
	return rates, nil
}

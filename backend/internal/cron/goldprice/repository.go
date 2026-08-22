package goldprice

import (
	"fmt"
	"time"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository stores gold price snapshots.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new gold price repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// Create stores a new gold price snapshot.
func (r *Repository) Create(price *domain.GoldPrice) (*domain.GoldPrice, error) {
	m := price.ToModel()
	if err := r.db.Create(m).Error; err != nil {
		return nil, err
	}
	return domain.FromGoldPriceModel(m), nil
}

// GetLatest returns the most recently stored snapshot.
func (r *Repository) GetLatest() (*domain.GoldPrice, error) {
	var m model.GoldPrice
	if err := r.db.Order("created_at desc").First(&m).Error; err != nil {
		return nil, err
	}
	return domain.FromGoldPriceModel(&m), nil
}

// GetHistory returns gold price snapshots stored within the given number of days, oldest first.
// Ranges shorter than a week return raw snapshots; longer ranges are downsampled to the last
// snapshot per hour (under a month) or per day.
func (r *Repository) GetHistory(days int) ([]*domain.GoldPrice, error) {
	var ms []model.GoldPrice
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
				FROM gold_prices
				WHERE created_at >= ?
				ORDER BY DATE_TRUNC('%s', created_at), created_at DESC
			) recent
			ORDER BY created_at ASC`, bucket, bucket), since).
			Scan(&ms).Error
	}
	if err != nil {
		return nil, err
	}

	prices := make([]*domain.GoldPrice, 0, len(ms))
	for i := range ms {
		prices = append(prices, domain.FromGoldPriceModel(&ms[i]))
	}
	return prices, nil
}

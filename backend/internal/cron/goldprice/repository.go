package goldprice

import (
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

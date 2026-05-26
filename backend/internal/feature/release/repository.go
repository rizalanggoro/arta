package release

import (
	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository handles release database operations.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new release repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// CreateRelease stores a new release.
func (r *Repository) CreateRelease(release *domain.Release) (*domain.Release, error) {
	releaseModel := release.ToModel()
	if err := r.db.Create(releaseModel).Error; err != nil {
		return nil, err
	}

	return domain.FromReleaseModel(releaseModel), nil
}

// GetLatestRelease returns the newest release by version code.
func (r *Repository) GetLatestRelease() (*domain.Release, error) {
	var releaseModel model.Release
	if err := r.db.Order("version_code desc, id desc").First(&releaseModel).Error; err != nil {
		return nil, err
	}

	return domain.FromReleaseModel(&releaseModel), nil
}

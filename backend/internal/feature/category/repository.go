package category

import (
	"time"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository handles category DB operations.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new category repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// CreateCategory inserts a new category.
func (r *Repository) CreateCategory(c *domain.Category) (*domain.Category, error) {
	m := c.ToModel()
	if err := r.db.Create(m).Error; err != nil {
		return nil, err
	}
	return domain.FromCategoryModel(m), nil
}

// GetCategoryByID fetches a category by numeric ID.
func (r *Repository) GetCategoryByID(id uint) (*domain.Category, error) {
	var m model.Category
	if err := r.db.First(&m, id).Error; err != nil {
		return nil, err
	}
	return domain.FromCategoryModel(&m), nil
}

type GetAllCategoriesFilter struct {
	UserId       uint
	WalletId     uint
	IncludeStats bool
	StartDate    time.Time
	EndDate      time.Time
}

func (r *Repository) GetAll(filter GetAllCategoriesFilter) ([]dto.Category, error) {
	var categories []struct {
		model.Category
		TotalAmount      float64
		TransactionCount int
	}

	query := r.db.Model(&model.Category{}).
		Where("categories.user_id is null or categories.user_id = ?", filter.UserId)

	if filter.IncludeStats && filter.WalletId != 0 {
		query = query.Select(`
				categories.*,
				COALESCE(SUM(transactions.amount), 0) as total_amount, 
				COUNT(transactions.*) as transaction_count
			`).
			Joins("join transactions on transactions.category_id = categories.id").
			Joins("join wallets on wallets.id = transactions.wallet_id").
			Group("categories.id").
			Where("wallets.id = ?", filter.WalletId)
	} else {
		query = query.Select("categories.*")
	}

	if !filter.StartDate.IsZero() {
		query = query.Where("transactions.date >= ?", filter.StartDate)
	}

	if !filter.EndDate.IsZero() {
		query = query.Where("transactions.date < ?", filter.EndDate)
	}

	if err := query.Order("lower(categories.name) asc").Find(&categories).Error; err != nil {
		return nil, err
	}

	result := make([]dto.Category, len(categories))
	for index, category := range categories {
		result[index] = dto.Category{
			Data:             *domain.FromCategoryModel(&category.Category),
			TotalAmount:      category.TotalAmount,
			TransactionCount: category.TransactionCount,
		}
	}

	return result, nil
}

// GetCategoriesByUserID returns default categories and those created by the user.
func (r *Repository) GetCategoriesByUserID(userID uint, categoryType string) ([]domain.Category, error) {
	var m []model.Category
	query := r.db.Where("(user_id IS NULL OR user_id = ?)", userID)
	if categoryType != "" {
		query = query.Where("type = ?", categoryType)
	} else {
		query = query.Where("type IN ?", []string{"income", "expense"})
	}
	if err := query.Find(&m).Error; err != nil {
		return nil, err
	}

	out := make([]domain.Category, 0, len(m))
	for i := range m {
		out = append(out, *domain.FromCategoryModel(&m[i]))
	}
	return out, nil
}

// UpdateCategory updates an existing category.
func (r *Repository) UpdateCategory(c *domain.Category) (*domain.Category, error) {
	m := c.ToModel()
	if err := r.db.Model(&model.Category{}).Where("id = ?", m.ID).Updates(m).Error; err != nil {
		return nil, err
	}
	var updated model.Category
	if err := r.db.First(&updated, m.ID).Error; err != nil {
		return nil, err
	}
	return domain.FromCategoryModel(&updated), nil
}

// DeleteCategory removes a category by id.
func (r *Repository) DeleteCategory(id uint) error {
	result := r.db.Delete(&model.Category{}, id)
	if result.Error != nil {
		return result.Error
	}
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}
	return nil
}

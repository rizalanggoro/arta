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

type GetCategoryFilterFilter struct {
	CategoryId          uint
	UserId              uint
	WalletId            uint
	IncludeTotalAmount  bool
	IncludeTransactions bool
	StartDate           time.Time
	EndDate             time.Time
}

func (r *Repository) Get(filter GetCategoryFilterFilter) (*dto.Category, error) {
	var category model.Category

	query := r.db.Model(&model.Category{}).
		Where("categories.id = ?", filter.CategoryId).
		Where("categories.user_id is null or categories.user_id = ?", filter.UserId)

	if filter.IncludeTotalAmount || filter.IncludeTransactions {
		query = query.Preload("Transactions", func(db *gorm.DB) *gorm.DB {
			if filter.WalletId != 0 {
				db = db.Where("transactions.wallet_id = ?", filter.WalletId)
			}

			if !filter.StartDate.IsZero() {
				db = db.Where("transactions.date >= ?", filter.StartDate)
			}

			if !filter.EndDate.IsZero() {
				db = db.Where("transactions.date < ?", filter.EndDate)
			}

			return db
		})
	}

	if err := query.First(&category).Error; err != nil {
		return nil, err
	}

	totalAmount := 0.0
	transactions := make([]domain.Transaction, 0)

	if filter.IncludeTransactions {
		transactions = make([]domain.Transaction, len(category.Transactions))
	}

	if filter.IncludeTotalAmount || filter.IncludeTransactions {
		for index, transaction := range category.Transactions {
			if filter.IncludeTotalAmount {
				totalAmount += transaction.Amount
			}

			if filter.IncludeTransactions {
				transactions[index] = *domain.FromTransactionModel(&transaction)
			}
		}
	}

	return &dto.Category{
		Data:         *domain.FromCategoryModel(&category),
		TotalAmount:  totalAmount,
		Transactions: transactions,
	}, nil
}

type GetAllCategoriesFilter struct {
	UserId       uint
	WalletId     uint
	Type         string
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

	if filter.Type != "" {
		query = query.Where("categories.type = ?", filter.Type)
	}

	if filter.IncludeStats && filter.WalletId != 0 {
		query = query.Select(`
				categories.*,
				COALESCE(SUM(transactions.amount), 0) as total_amount, 
				COUNT(transactions.*) as transaction_count
			`).
			Joins("join transactions on transactions.category_id = categories.id").
			Group("categories.id").
			Where("transactions.wallet_id = ?", filter.WalletId).
			Where("transactions.deleted_at is null")
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

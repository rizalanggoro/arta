package transaction

import (
	"time"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository handles transaction DB operations.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new transaction repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// CreateTransaction inserts a new transaction.
func (r *Repository) CreateTransaction(t *domain.Transaction) (*domain.Transaction, error) {
	m := t.ToModel()
	if err := r.db.Create(m).Error; err != nil {
		return nil, err
	}
	return domain.FromTransactionModel(m), nil
}

type GetFilter struct {
	TransactionId   uint
	IncludeCategory bool
}

func (r *Repository) Get(filter GetFilter) (*dto.Transaction, error) {
	var transaction model.Transaction

	query := r.db.Where("id = ?", filter.TransactionId)
	if filter.IncludeCategory {
		query = query.Preload("Category")
	}

	if err := query.First(&transaction).Error; err != nil {
		return nil, err
	} else {
		return &dto.Transaction{
			Data:     *domain.FromTransactionModel(&transaction),
			Category: *domain.FromCategoryModel(&transaction.Category),
		}, nil
	}
}

// GetTransactionByID fetches a transaction by numeric ID.
// Deprecated: use get
func (r *Repository) GetTransactionByID(id uint) (*domain.Transaction, error) {
	var m model.Transaction
	if err := r.db.First(&m, id).Error; err != nil {
		return nil, err
	}
	return domain.FromTransactionModel(&m), nil
}

// GetAll returns transactions for a wallet.
type GetAllFilter struct {
	WalletId        uint
	IncludeCategory bool
	Limit           int
	OrderBy         string
	OrderDirection  string
}

func (r *Repository) GetAll(filter *GetAllFilter) ([]dto.Transaction, error) {
	var transactions []model.Transaction

	query := r.db.Where("wallet_id = ?", filter.WalletId)
	if filter.Limit > 0 {
		query = query.Limit(filter.Limit)
	}
	if filter.OrderBy != "" {
		orderDir := "asc"
		if filter.OrderDirection != "" {
			orderDir = filter.OrderDirection
		}
		query = query.Order(filter.OrderBy + " " + orderDir).
			Order("created_at desc")
	}
	if filter.IncludeCategory {
		query = query.Preload("Category")
	}

	if err := query.Find(&transactions).Error; err != nil {
		return nil, err
	} else {
		result := make([]dto.Transaction, len(transactions))
		for index, transaction := range transactions {
			result[index] = dto.Transaction{
				Data:     *domain.FromTransactionModel(&transaction),
				Category: *domain.FromCategoryModel(&transaction.Category),
			}
		}
		return result, nil
	}
}

type GetCurrentBalanceFilter struct {
	WalletId uint
}

func (r *Repository) GetCurrentBalance(filter GetCurrentBalanceFilter) (*float64, error) {
	var currentBalance float64
	if err := r.db.Model(&model.Transaction{}).
		Joins("JOIN categories on categories.id = transactions.category_id").
		Select(`
			COALESCE(
				SUM(
					CASE
						WHEN categories.type = 'income' THEN transactions.amount
						WHEN categories.type = 'expense' THEN -transactions.amount
						ELSE 0 
					END
				), 0
			)
		`).
		Where("wallet_id = ?", filter.WalletId).
		Find(&currentBalance).Error; err != nil {
		return nil, err
	} else {
		return &currentBalance, nil
	}
}

type GetTotalIncomeExpenseFilter struct {
	WalletId  uint
	StartDate time.Time
	EndDate   time.Time
}

func (r *Repository) GetTotalIncomeExpense(filter GetTotalIncomeExpenseFilter) (*float64, *float64, error) {
	var result struct {
		Income  float64
		Expense float64
	}

	if err := r.db.Model(&model.Transaction{}).
		Joins("JOIN categories on categories.id = transactions.category_id").
		Select(`
			COALESCE(
				SUM(
					CASE
						WHEN categories.type = 'income' THEN transactions.amount
						ELSE 0 
					END
				), 0
			) as income, 
			COALESCE(
				SUM(
					CASE
						WHEN categories.type = 'expense' THEN transactions.amount
						ELSE 0 
					END
				), 0
			) as expense
		`).
		Where("wallet_id = ?", filter.WalletId).
		Where("transactions.date >= ?", filter.StartDate).
		Where("transactions.date < ?", filter.EndDate).
		Find(&result).Error; err != nil {
		return nil, nil, err
	} else {
		return &result.Income, &result.Expense, nil
	}
}

// UpdateTransaction updates an existing transaction.
func (r *Repository) UpdateTransaction(t *domain.Transaction) (*domain.Transaction, error) {
	m := t.ToModel()
	if err := r.db.Model(&model.Transaction{}).Where("id = ?", m.ID).Updates(m).Error; err != nil {
		return nil, err
	}
	var updated model.Transaction
	if err := r.db.First(&updated, m.ID).Error; err != nil {
		return nil, err
	}
	return domain.FromTransactionModel(&updated), nil
}

// DeleteTransaction removes a transaction by id.
func (r *Repository) DeleteTransaction(id uint) error {
	result := r.db.Delete(&model.Transaction{}, id)
	if result.Error != nil {
		return result.Error
	}
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}
	return nil
}

// GetWalletOwnerID returns the owner user id for a wallet.
func (r *Repository) GetWalletOwnerID(walletID uint) (uint, error) {
	var w model.Wallet
	if err := r.db.First(&w, walletID).Error; err != nil {
		return 0, err
	}
	return w.UserID, nil
}

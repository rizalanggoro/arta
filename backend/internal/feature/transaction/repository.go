package transaction

import (
	"github.com/artafinance/backend/internal/domain"
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

// GetTransactionByID fetches a transaction by numeric ID.
func (r *Repository) GetTransactionByID(id uint) (*domain.Transaction, error) {
	var m model.Transaction
	if err := r.db.First(&m, id).Error; err != nil {
		return nil, err
	}
	return domain.FromTransactionModel(&m), nil
}

// GetTransactionsByWalletID returns transactions for a wallet.
func (r *Repository) GetTransactionsByWalletID(walletID uint) ([]domain.Transaction, error) {
	var m []model.Transaction
	if err := r.db.Where("wallet_id = ?", walletID).Order("date desc").Find(&m).Error; err != nil {
		return nil, err
	}

	out := make([]domain.Transaction, 0, len(m))
	for i := range m {
		out = append(out, *domain.FromTransactionModel(&m[i]))
	}
	return out, nil
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

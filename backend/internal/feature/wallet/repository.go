package wallet

import (
	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository handles wallet DB operations.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new wallet repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// CreateWallet inserts a new wallet.
func (r *Repository) CreateWallet(w *domain.Wallet) (*domain.Wallet, error) {
	m := w.ToModel()
	if err := r.db.Create(m).Error; err != nil {
		return nil, err
	}
	return domain.FromWalletModel(m), nil
}

// GetWalletByID fetches a wallet by numeric ID.
func (r *Repository) GetWalletByID(id uint) (*domain.Wallet, error) {
	var m model.Wallet
	if err := r.db.First(&m, id).Error; err != nil {
		return nil, err
	}
	return domain.FromWalletModel(&m), nil
}

// GetWalletsByUserID returns all wallets for a user.
func (r *Repository) GetWalletsByUserID(userID uint) ([]domain.Wallet, error) {
	var m []model.Wallet
	if err := r.db.Where("user_id = ?", userID).Find(&m).Error; err != nil {
		return nil, err
	}

	out := make([]domain.Wallet, 0, len(m))
	for i := range m {
		out = append(out, *domain.FromWalletModel(&m[i]))
	}
	return out, nil
}

// UpdateWallet updates an existing wallet.
func (r *Repository) UpdateWallet(w *domain.Wallet) (*domain.Wallet, error) {
	m := w.ToModel()
	if err := r.db.Model(&model.Wallet{}).Where("id = ?", m.ID).Updates(m).Error; err != nil {
		return nil, err
	}
	var updated model.Wallet
	if err := r.db.First(&updated, m.ID).Error; err != nil {
		return nil, err
	}
	return domain.FromWalletModel(&updated), nil
}

// DeleteWallet removes a wallet by id.
func (r *Repository) DeleteWallet(id uint) error {
	result := r.db.Delete(&model.Wallet{}, id)
	if result.Error != nil {
		return result.Error
	}
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}
	return nil
}

// SetDefaultWallet sets the given wallet as default and clears others for the user.
func (r *Repository) SetDefaultWallet(userID uint, walletID uint) error {
	// clear other defaults
	if err := r.db.Model(&model.Wallet{}).Where("user_id = ?", userID).Update("is_default", false).Error; err != nil {
		return err
	}
	// set this wallet
	if err := r.db.Model(&model.Wallet{}).Where("id = ?", walletID).Update("is_default", true).Error; err != nil {
		return err
	}
	return nil
}

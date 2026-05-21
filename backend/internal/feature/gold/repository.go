package gold

import (
	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository handles gold DB operations.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new gold repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// CreateGold inserts a new gold entry.
func (r *Repository) CreateGold(g *domain.Gold) (*domain.Gold, error) {
	m := g.ToModel()
	if err := r.db.Create(m).Error; err != nil {
		return nil, err
	}
	return domain.FromGoldModel(m), nil
}

// GetGoldByID fetches a gold entry by ID.
func (r *Repository) GetGoldByID(id uint) (*domain.Gold, error) {
	var m model.Gold
	if err := r.db.First(&m, id).Error; err != nil {
		return nil, err
	}
	return domain.FromGoldModel(&m), nil
}

// GetGoldsByWalletID returns gold entries for a wallet.
func (r *Repository) GetGoldsByWalletID(walletID uint) ([]domain.Gold, error) {
	var m []model.Gold
	if err := r.db.Where("wallet_id = ?", walletID).Order("date desc").Find(&m).Error; err != nil {
		return nil, err
	}
	out := make([]domain.Gold, 0, len(m))
	for i := range m {
		out = append(out, *domain.FromGoldModel(&m[i]))
	}
	return out, nil
}

// GetGoldsByUserID returns all golds for a user's wallets.
func (r *Repository) GetGoldsByUserID(userID uint) ([]domain.Gold, error) {
	var m []model.Gold
	if err := r.db.Joins("JOIN wallets ON wallets.id = golds.wallet_id").Where("wallets.user_id = ?", userID).Order("date desc").Find(&m).Error; err != nil {
		return nil, err
	}
	out := make([]domain.Gold, 0, len(m))
	for i := range m {
		out = append(out, *domain.FromGoldModel(&m[i]))
	}
	return out, nil
}

// UpdateGold updates an existing gold entry.
func (r *Repository) UpdateGold(g *domain.Gold) (*domain.Gold, error) {
	m := g.ToModel()
	if err := r.db.Model(&model.Gold{}).Where("id = ?", m.ID).Updates(m).Error; err != nil {
		return nil, err
	}
	var updated model.Gold
	if err := r.db.First(&updated, m.ID).Error; err != nil {
		return nil, err
	}
	return domain.FromGoldModel(&updated), nil
}

// DeleteGold removes a gold entry by id.
func (r *Repository) DeleteGold(id uint) error {
	result := r.db.Delete(&model.Gold{}, id)
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

// GetSummary computes total grams and total value grouped by type for a user.
func (r *Repository) GetSummary(userID uint) (float64, float64, map[string]interface{}, error) {
	golds, err := r.GetGoldsByUserID(userID)
	if err != nil {
		return 0, 0, nil, err
	}
	var totalGrams, totalValue float64
	byType := make(map[string]interface{})
	// simple aggregation
	typeAgg := make(map[string]map[string]float64)
	for _, g := range golds {
		totalGrams += g.Grams
		totalValue += g.Price
		if _, ok := typeAgg[g.Type]; !ok {
			typeAgg[g.Type] = map[string]float64{"grams": 0, "value": 0}
		}
		typeAgg[g.Type]["grams"] += g.Grams
		typeAgg[g.Type]["value"] += g.Price
	}
	for k, v := range typeAgg {
		byType[k] = v
	}
	return totalGrams, totalValue, byType, nil
}

// GetTaxPreferencesByUserID returns gold tax preferences for a user.
func (r *Repository) GetTaxPreferencesByUserID(userID uint) ([]domain.GoldTaxPreference, error) {
	var records []model.GoldTaxPreference
	if err := r.db.Where("user_id = ?", userID).Order("carat asc").Find(&records).Error; err != nil {
		return nil, err
	}

	result := make([]domain.GoldTaxPreference, 0, len(records))
	for i := range records {
		result = append(result, *domain.FromGoldTaxPreferenceModel(&records[i]))
	}

	return result, nil
}

// CreateTaxPreference inserts a new gold tax preference for a user.
func (r *Repository) CreateTaxPreference(userID uint, preference *domain.GoldTaxPreference) (*domain.GoldTaxPreference, error) {
	if preference == nil {
		return nil, gorm.ErrInvalidData
	}

	preference.UserID = userID
	modelValue := preference.ToModel()
	if err := r.db.Create(modelValue).Error; err != nil {
		return nil, err
	}

	return r.GetTaxPreferenceByID(userID, modelValue.ID)
}

// GetTaxPreferenceByID returns a single gold tax preference owned by the user.
func (r *Repository) GetTaxPreferenceByID(userID, id uint) (*domain.GoldTaxPreference, error) {
	var record model.GoldTaxPreference
	if err := r.db.Where("id = ? AND user_id = ?", id, userID).First(&record).Error; err != nil {
		return nil, err
	}

	return domain.FromGoldTaxPreferenceModel(&record), nil
}

// UpdateTaxPreference updates an existing gold tax preference for a user.
func (r *Repository) UpdateTaxPreference(userID uint, preference *domain.GoldTaxPreference) (*domain.GoldTaxPreference, error) {
	if preference == nil {
		return nil, gorm.ErrInvalidData
	}

	updates := map[string]any{
		"carat":      preference.Carat,
		"tax_rate":   preference.TaxRate,
		"updated_at": gorm.Expr("CURRENT_TIMESTAMP"),
	}

	result := r.db.Model(&model.GoldTaxPreference{}).
		Where("id = ? AND user_id = ?", preference.ID, userID).
		Updates(updates)
	if result.Error != nil {
		return nil, result.Error
	}
	if result.RowsAffected == 0 {
		return nil, gorm.ErrRecordNotFound
	}

	return r.GetTaxPreferenceByID(userID, preference.ID)
}

// DeleteTaxPreference deletes an existing gold tax preference for a user.
func (r *Repository) DeleteTaxPreference(userID, id uint) error {
	result := r.db.Where("id = ? AND user_id = ?", id, userID).Delete(&model.GoldTaxPreference{})
	if result.Error != nil {
		return result.Error
	}
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}
	return nil
}

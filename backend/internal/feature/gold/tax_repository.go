package gold

import (
	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/dto"
	"github.com/artafinance/backend/internal/model"
	"github.com/artafinance/backend/pkg/config"
	"github.com/artafinance/backend/pkg/constant"
	"gorm.io/gorm"
)

type GoldTaxRepository struct {
	db     *gorm.DB
	config *config.Config
}

func NewGoldTaxRepository(
	db *gorm.DB,
	config *config.Config,
) *GoldTaxRepository {
	return &GoldTaxRepository{
		db:     db,
		config: config,
	}
}

type GetAllGoldTaxesFilter struct {
	UserId uint
}

func (r *GoldTaxRepository) GetAllGoldTaxes(filter GetAllGoldTaxesFilter) ([]dto.GoldTax, error) {
	var goldTaxes []struct {
		model.GoldTaxPreference
		SellPrice float64
	}

	if err := r.db.Model(&model.GoldTaxPreference{}).
		Select(`
			gold_tax_preferences.*, 
			(
				-- harga per gram dalam IDR 
				((gp.price_per_ounce_usd / ?) * fr.rate) *
				-- purity  
				(gold_tax_preferences.carat / 24.0) *
				-- retail multiplier 
				? * 
				-- tax
				(1 - (gold_tax_preferences.tax_rate / 100.0))
			) as sell_price
		`,
			constant.GramsPerTroyOunce,
			r.config.GoldRetailMultiplier,
		).
		Joins("left join (select * from gold_prices order by created_at desc limit 1) as gp on true").
		Joins("left join (select * from fx_rates order by created_at desc limit 1) as fr on true").
		Where("user_id = ?", filter.UserId).
		Find(&goldTaxes).
		Error; err != nil {
		return nil, err
	} else {
		result := make([]dto.GoldTax, len(goldTaxes))
		for index, goldTax := range goldTaxes {
			result[index] = dto.GoldTax{
				Data:      *domain.FromGoldTaxPreferenceModel(&goldTax.GoldTaxPreference),
				SellPrice: goldTax.SellPrice,
			}
		}

		return result, nil
	}
}

package dashboard

import (
	"github.com/artafinance/backend/internal/model"
	"github.com/artafinance/backend/pkg/config"
	"github.com/artafinance/backend/pkg/constant"
	"gorm.io/gorm"
)

type DashboardGoldRepository struct {
	db     *gorm.DB
	config *config.Config
}

func NewDashboardGoldRepository(
	db *gorm.DB,
	config *config.Config,
) *DashboardGoldRepository {
	return &DashboardGoldRepository{
		db:     db,
		config: config,
	}
}

type GetTotalSellPriceFilter struct {
	WalletId uint
	UserId   uint
}

func (r *DashboardGoldRepository) GetTotalSellPrice(filter GetTotalSellPriceFilter) (*float64, error) {
	var totalSellPrice float64
	if err := r.db.Model(&model.Gold{}).
		Select(`
			coalesce(
				sum(
					-- gramasi 
					golds.grams *
					-- harga per gram dalam IDR 
					((gp.price_per_ounce_usd / ?) * fr.rate) *
					-- purity  
					(golds.carat / 24.0) *
					-- retail multiplier 
					? * 
					-- tax
					(1 - (coalesce(gtp.tax_rate, 0) / 100.0))
				), 0
			)
		`,
			constant.GramsPerTroyOunce,
			r.config.GoldRetailMultiplier,
		).
		Joins("left join (select * from gold_prices order by created_at desc limit 1) as gp on true").
		Joins("left join (select * from fx_rates order by created_at desc limit 1) as fr on true").
		Joins("left join gold_tax_preferences as gtp on gtp.carat = golds.carat and gtp.user_id = ?", filter.UserId).
		Where("wallet_id = ?", filter.WalletId).
		Find(&totalSellPrice).
		Error; err != nil {
		return nil, err
	} else {
		return &totalSellPrice, nil
	}
}

type GetTotalBuyPriceFilter struct {
	WalletId uint
}

func (r *DashboardGoldRepository) GetTotalBuyPrice(filter GetTotalBuyPriceFilter) (*float64, error) {
	var totalBuyPrice float64
	if err := r.db.Model(&model.Gold{}).
		Select("coalesce(sum(golds.price), 0)").
		Where("wallet_id = ?", filter.WalletId).
		Find(&totalBuyPrice).
		Error; err != nil {
		return nil, err
	} else {
		return &totalBuyPrice, nil
	}
}

type GetTotalWeightFilter struct {
	WalletId uint
}

func (r *DashboardGoldRepository) GetTotalWeight(filter GetTotalWeightFilter) (*float64, error) {
	var totalWeight float64
	if err := r.db.Model(&model.Gold{}).
		Select("coalesce(sum(golds.grams), 0)").
		Where("wallet_id = ?", filter.WalletId).
		Find(&totalWeight).
		Error; err != nil {
		return nil, err
	} else {
		return &totalWeight, nil
	}
}

type GetItemCountFilter struct {
	WalletId uint
}

func (r *DashboardGoldRepository) GetItemCount(filter GetItemCountFilter) (*int, error) {
	var itemCount int
	if err := r.db.Model(&model.Gold{}).
		Select("count(*)").
		Where("wallet_id = ?", filter.WalletId).
		Find(&itemCount).
		Error; err != nil {
		return nil, err
	} else {
		return &itemCount, nil
	}
}

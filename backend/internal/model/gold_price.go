package model

import (
	"time"

	"gorm.io/gorm"
)

// GoldPrice represents gold price history.
type GoldPrice struct {
	gorm.Model
	Symbol            string    `gorm:"not null;type:varchar(8);default:'XAU'"`
	Currency          string    `gorm:"not null;type:varchar(3);default:'USD'"`
	CurrencySymbol    string    `gorm:"not null;type:varchar(8);default:'$'"`
	ExchangeRate      float64   `gorm:"not null;type:numeric(18,6);default:1"`
	PricePerOunceUSD  float64   `gorm:"not null;type:numeric(18,6)"`
	SourceUpdatedAt   time.Time `gorm:"not null;index"`
	SourceReadableAge string    `gorm:"not null;type:varchar(64);default:''"`
}

// TableName specifies the table name for GoldPrice model.
func (GoldPrice) TableName() string {
	return "gold_prices"
}

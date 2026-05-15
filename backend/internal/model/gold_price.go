package model

import (
	"time"

	"gorm.io/gorm"
)

// GoldPrice represents gold price history.
type GoldPrice struct {
	gorm.Model
	Date         time.Time `gorm:"not null;index"`
	PricePerGram float64   `gorm:"not null;type:numeric(15,2)"`
	Currency     string    `gorm:"not null;type:varchar(3);default:'IDR'"`
}

// TableName specifies the table name for GoldPrice model.
func (GoldPrice) TableName() string {
	return "gold_prices"
}

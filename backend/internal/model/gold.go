package model

import (
	"time"

	"gorm.io/gorm"
)

// Gold represents gold holdings.
type Gold struct {
	gorm.Model
	WalletID uint      `gorm:"not null;index"`
	Date     time.Time `gorm:"not null;index"`
	Grams    float64   `gorm:"not null;type:numeric(10,3)"`
	// Price is the total purchase price for this gold entry (e.g. Grams * price_per_gram at purchase time)
	Price         float64 `gorm:"not null;type:numeric(15,2)"`
	Type          string  `gorm:"not null;type:varchar(100)"` // pure_24k, jewelry_ring, etc
	PurityPercent float64 `gorm:"type:numeric(5,2)"`          // For reference
	Notes         string  `gorm:"type:text"`

	// Foreign key
	Wallet *Wallet `gorm:"foreignKey:WalletID;references:ID;constraint:OnDelete:CASCADE"`
}

// TableName specifies the table name for Gold model.
func (Gold) TableName() string {
	return "golds"
}

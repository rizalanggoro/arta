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
	Price    uint64    `gorm:"not null"`
	Type     string    `gorm:"not null;type:varchar(100)"` // pure_gold or jewelry
	Carat    float64   `gorm:"type:numeric(5,2)"`
	Notes    string    `gorm:"type:text"`

	// Foreign key
	Wallet *Wallet `gorm:"foreignKey:WalletID;references:ID;constraint:OnDelete:CASCADE"`
}

// TableName specifies the table name for Gold model.
func (Gold) TableName() string {
	return "golds"
}

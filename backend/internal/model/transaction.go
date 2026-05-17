package model

import (
	"time"

	"gorm.io/gorm"
)

// Transaction represents a financial transaction.
type Transaction struct {
	gorm.Model
	WalletID    uint      `gorm:"not null;index"`
	Amount      float64   `gorm:"not null;type:numeric(15,2)"`
	CategoryID  uint      `gorm:"not null;index"`
	Description string    `gorm:"type:text"`
	Date        time.Time `gorm:"not null;index"`

	// Foreign keys
	Wallet   *Wallet   `gorm:"foreignKey:WalletID;references:ID;constraint:OnDelete:CASCADE"`
	Category *Category `gorm:"foreignKey:CategoryID;references:ID;constraint:OnDelete:RESTRICT"`
}

// TableName specifies the table name for Transaction model.
func (Transaction) TableName() string {
	return "transactions"
}

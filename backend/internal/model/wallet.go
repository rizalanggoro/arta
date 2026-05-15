package model

import "gorm.io/gorm"

// Wallet represents a financial wallet owned by a user.
type Wallet struct {
	gorm.Model
	UserID    uint   `gorm:"not null;index"`
	Name      string `gorm:"not null;type:varchar(255)"`
	Type      string `gorm:"not null;type:varchar(50);index"` // cash_savings or gold_savings
	IsDefault bool   `gorm:"not null;default:false"`

	// Relations
	User         *User         `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
	Transactions []Transaction `gorm:"foreignKey:WalletID;references:ID;constraint:OnDelete:CASCADE"`
	Golds        []Gold        `gorm:"foreignKey:WalletID;references:ID;constraint:OnDelete:CASCADE"`
}

// TableName specifies the table name for Wallet model.
func (Wallet) TableName() string {
	return "wallets"
}

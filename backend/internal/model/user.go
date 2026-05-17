package model

import "gorm.io/gorm"

// User represents a user account in the system.
type User struct {
	gorm.Model
	Email    string `gorm:"uniqueIndex;not null;type:varchar(255)"`
	Name     string `gorm:"not null;type:varchar(255)"`
	Password string `gorm:"not null;type:varchar(255)"` // bcrypt hashed

	// Relations
	Wallets    []Wallet   `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
	Categories []Category `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
	Sessions   []Session  `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
}

// TableName specifies the table name for User model.
func (User) TableName() string {
	return "users"
}

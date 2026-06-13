package model

import "gorm.io/gorm"

// Category represents transaction categories.
type Category struct {
	gorm.Model
	UserID *uint  `gorm:"index"` // NULL for default categories
	Name   string `gorm:"not null;type:varchar(255)"`
	Type   string `gorm:"not null;type:varchar(50)"` // income, expense
	// Use UserID==NULL to indicate system default categories.

	// Foreign key
	User         *User `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
	Transactions []Transaction
}

// TableName specifies the table name for Category model.
func (Category) TableName() string {
	return "categories"
}

package model

import "gorm.io/gorm"

// Category represents transaction categories.
type Category struct {
	gorm.Model
	UserID    *uint  `gorm:"index"` // NULL for default categories
	Name      string `gorm:"not null;type:varchar(255)"`
	Type      string `gorm:"not null;type:varchar(50)"` // income, expense, general
	Icon      string `gorm:"type:varchar(255)"`
	Color     string `gorm:"type:varchar(10)"`       // hex color
	IsCustom  bool   `gorm:"not null;default:false"` // true for user-created
	IsDefault bool   `gorm:"not null;default:false"` // true for system defaults

	// Foreign key
	User *User `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
}

// TableName specifies the table name for Category model.
func (Category) TableName() string {
	return "categories"
}

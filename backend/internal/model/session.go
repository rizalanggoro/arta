package model

import "gorm.io/gorm"

// Session represents a stored JWT session token.
type Session struct {
	gorm.Model
	UserID uint   `gorm:"not null;index"`
	Token  string `gorm:"not null;type:text;uniqueIndex"`

	// Foreign key
	User *User `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
}

// TableName specifies the table name for Session model.
func (Session) TableName() string {
	return "sessions"
}

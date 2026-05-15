package model

import (
	"time"

	"gorm.io/gorm"
)

// Session represents JWT session tokens.
type Session struct {
	gorm.Model
	UserID    uint      `gorm:"not null;index"`
	Token     string    `gorm:"not null;type:text;uniqueIndex"`
	TokenType string    `gorm:"not null;type:varchar(50);default:'Bearer'"`
	ExpiresAt time.Time `gorm:"not null;index"`
	Revoked   bool      `gorm:"not null;default:false"`

	// Foreign key
	User *User `gorm:"foreignKey:UserID;references:ID;constraint:OnDelete:CASCADE"`
}

// TableName specifies the table name for Session model.
func (Session) TableName() string {
	return "sessions"
}

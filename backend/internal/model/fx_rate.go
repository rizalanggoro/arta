package model

import (
	"time"

	"gorm.io/gorm"
)

// FxRate represents a foreign exchange rate snapshot.
type FxRate struct {
	gorm.Model
	Base string    `gorm:"not null;type:varchar(3);index"`
	Date time.Time `gorm:"not null;type:date;index"`
	Rate int       `gorm:"not null"`
}

// TableName specifies the table name for FxRate model.
func (FxRate) TableName() string {
	return "fx_rates"
}

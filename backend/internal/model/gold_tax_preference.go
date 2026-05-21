package model

import "gorm.io/gorm"

// GoldTaxPreference stores the tax preference for a user's gold by carat.
type GoldTaxPreference struct {
	gorm.Model
	UserID  uint    `gorm:"not null;index:idx_gold_tax_preferences_user_carat,unique"`
	Carat   float64 `gorm:"not null;type:numeric(5,2);index:idx_gold_tax_preferences_user_carat,unique"`
	TaxRate float64 `gorm:"not null;type:numeric(5,2)"`
}

// TableName specifies the table name for GoldTaxPreference model.
func (GoldTaxPreference) TableName() string {
	return "gold_tax_preferences"
}

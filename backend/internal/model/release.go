package model

import "gorm.io/gorm"

// Release represents an application release entry.
type Release struct {
	gorm.Model
	URL         string `gorm:"not null;type:text"`
	VersionCode int    `gorm:"not null;index"`
}

// TableName specifies the table name for Release model.
func (Release) TableName() string {
	return "releases"
}
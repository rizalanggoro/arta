package database

import (
	"fmt"
	"log"

	"github.com/artafinance/backend/internal/model"
	"github.com/artafinance/backend/pkg/config"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

var db *gorm.DB

// Initialize initializes database connection and runs migrations
func Initialize(cfg *config.Config) (*gorm.DB, error) {
	var dialector gorm.Dialector
	dialector = postgres.Open(cfg.GetDatabaseURL())

	logLevel := logger.Silent
	if cfg.IsDevelopment() {
		logLevel = logger.Info
	}

	var err error
	db, err = gorm.Open(dialector, &gorm.Config{
		Logger: logger.Default.LogMode(logLevel),
	})
	if err != nil {
		return nil, fmt.Errorf("failed to connect to database: %w", err)
	}

	log.Println("Database connection established")

	// Auto migrate models
	if err := runMigrations(db); err != nil {
		return nil, fmt.Errorf("failed to run migrations: %w", err)
	}

	return db, nil
}

// GetDB returns the database instance
func GetDB() *gorm.DB {
	return db
}

// runMigrations runs all database migrations
func runMigrations(database *gorm.DB) error {
	return database.AutoMigrate(
		&model.User{},
		&model.Wallet{},
		&model.Session{},
		&model.Category{},
		&model.Transaction{},
		&model.Gold{},
		&model.GoldPrice{},
	)
}

// Close closes the database connection
func Close() error {
	if db == nil {
		return nil
	}
	sqlDB, err := db.DB()
	if err != nil {
		return err
	}
	return sqlDB.Close()
}

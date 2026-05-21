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
	if err := database.AutoMigrate(
		&model.User{},
		&model.Wallet{},
		&model.Session{},
		&model.Category{},
		&model.Transaction{},
		&model.Gold{},
		&model.GoldPrice{},
		&model.FxRate{},
	); err != nil {
		return err
	}

	return backfillGoldCarat(database)
}

func backfillGoldCarat(database *gorm.DB) error {
	var hasPurityPercent int64
	if err := database.Raw(`
		SELECT COUNT(*)
		FROM information_schema.columns
		WHERE table_schema = current_schema()
			AND table_name = 'golds'
			AND column_name = 'purity_percent'
	`).Scan(&hasPurityPercent).Error; err != nil {
		return err
	}

	var hasCarat int64
	if err := database.Raw(`
		SELECT COUNT(*)
		FROM information_schema.columns
		WHERE table_schema = current_schema()
			AND table_name = 'golds'
			AND column_name = 'carat'
	`).Scan(&hasCarat).Error; err != nil {
		return err
	}

	if hasPurityPercent == 0 || hasCarat == 0 {
		return nil
	}

	return database.Exec(`
		UPDATE golds
		SET carat = COALESCE(carat, purity_percent)
		WHERE carat IS NULL OR carat = 0
	`).Error
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

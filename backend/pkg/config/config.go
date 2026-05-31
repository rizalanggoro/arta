package config

import (
	"fmt"
	"os"
	"strconv"
)

// Config holds all configuration for the application
type Config struct {
	// Server
	ServerPort string
	ServerEnv  string

	// Database
	DBHost      string
	DBPort      string
	DBUser      string
	DBPassword  string
	DBName      string
	AppTimeZone string

	// JWT
	JWTSecret     string
	JWTExpiration int64 // in hours

	// Email (for password reset - future)
	EmailService  string
	EmailFrom     string
	EmailPassword string
}

// New creates a new Config instance from environment variables
func New() *Config {
	return &Config{
		ServerPort:    getEnv("SERVER_PORT", "3000"),
		ServerEnv:     getEnv("SERVER_ENV", "development"),
		DBHost:        getEnv("DB_HOST", "localhost"),
		DBPort:        getEnv("DB_PORT", "5432"),
		DBUser:        getEnv("DB_USER", "postgres"),
		DBPassword:    getEnv("DB_PASSWORD", "postgres"),
		DBName:        getEnv("DB_NAME", "arta"),
		AppTimeZone:   getEnv("APP_TIMEZONE", "Asia/Jakarta"),
		JWTSecret:     getEnv("JWT_SECRET", "your-secret-key-change-in-production"),
		JWTExpiration: getEnvInt("JWT_EXPIRATION", 168), // 7 days in hours
		EmailService:  getEnv("EMAIL_SERVICE", ""),
		EmailFrom:     getEnv("EMAIL_FROM", ""),
		EmailPassword: getEnv("EMAIL_PASSWORD", ""),
	}
}

// GetDatabaseURL returns the PostgreSQL connection string
func (c *Config) GetDatabaseURL() string {
	return fmt.Sprintf(
		"host=%s port=%s user=%s password=%s dbname=%s sslmode=disable TimeZone=%s",
		c.DBHost,
		c.DBPort,
		c.DBUser,
		c.DBPassword,
		c.DBName,
		c.AppTimeZone,
	)
}

// getEnv gets an environment variable with a default value
func getEnv(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}

// getEnvInt gets an environment variable as integer with a default value
func getEnvInt(key string, defaultValue int64) int64 {
	valueStr := getEnv(key, "")
	if valueStr == "" {
		return defaultValue
	}
	if value, err := strconv.ParseInt(valueStr, 10, 64); err == nil {
		return value
	}
	return defaultValue
}

// IsDevelopment returns true if server environment is development
func (c *Config) IsDevelopment() bool {
	return c.ServerEnv == "development"
}

// IsProduction returns true if server environment is production
func (c *Config) IsProduction() bool {
	return c.ServerEnv == "production"
}

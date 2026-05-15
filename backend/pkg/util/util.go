package util

import (
	"strings"

	"golang.org/x/crypto/bcrypt"
)

// HashPassword hashes a password using bcrypt
func HashPassword(password string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}
	return string(hashedPassword), nil
}

// VerifyPassword verifies a password against its bcrypt hash
func VerifyPassword(hashedPassword, password string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hashedPassword), []byte(password))
	return err == nil
}

// ValidateEmail validates email format (basic validation)
func ValidateEmail(email string) bool {
	email = strings.TrimSpace(email)
	return len(email) > 0 && len(email) <= 255 && strings.Contains(email, "@")
}

// ValidatePassword validates password requirements
func ValidatePassword(password string) bool {
	// Password must be at least 8 characters
	if len(password) < 8 {
		return false
	}
	// TODO: Add more validation (uppercase, lowercase, numbers, special chars)
	return true
}

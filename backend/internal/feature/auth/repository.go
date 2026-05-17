package auth

import (
	"errors"
	"strings"

	"github.com/artafinance/backend/internal/domain"
	"github.com/artafinance/backend/internal/model"
	"gorm.io/gorm"
)

// Repository handles authentication database operations.
type Repository struct {
	db *gorm.DB
}

// NewRepository creates a new auth repository.
func NewRepository(db *gorm.DB) *Repository {
	return &Repository{db: db}
}

// CreateUser stores a new user.
func (r *Repository) CreateUser(user *domain.User) (*domain.User, error) {
	userModel := user.ToModel()
	if err := r.db.Create(userModel).Error; err != nil {
		return nil, err
	}

	return domain.FromUserModel(userModel), nil
}

// GetUserByEmail fetches a user by email.
func (r *Repository) GetUserByEmail(email string) (*domain.User, error) {
	var userModel model.User
	normalizedEmail := strings.ToLower(strings.TrimSpace(email))
	if err := r.db.Where("LOWER(email) = ?", normalizedEmail).First(&userModel).Error; err != nil {
		return nil, err
	}

	return domain.FromUserModel(&userModel), nil
}

// GetUserByID fetches a user by ID.
func (r *Repository) GetUserByID(id uint) (*domain.User, error) {
	var userModel model.User
	if err := r.db.First(&userModel, id).Error; err != nil {
		return nil, err
	}

	return domain.FromUserModel(&userModel), nil
}

// CreateSession stores a new session.
func (r *Repository) CreateSession(session *domain.Session) (*domain.Session, error) {
	sessionModel := session.ToModel()
	if err := r.db.Create(sessionModel).Error; err != nil {
		return nil, err
	}

	return domain.FromSessionModel(sessionModel), nil
}

// GetSessionByToken fetches a session by token.
func (r *Repository) GetSessionByToken(token string) (*domain.Session, error) {
	var sessionModel model.Session
	if err := r.db.Where("token = ?", token).First(&sessionModel).Error; err != nil {
		return nil, err
	}

	return domain.FromSessionModel(&sessionModel), nil
}

// DeleteSessionByToken removes a session token from storage.
func (r *Repository) DeleteSessionByToken(token string) error {
	result := r.db.Where("token = ?", token).Delete(&model.Session{})
	if result.Error != nil {
		return result.Error
	}
	if result.RowsAffected == 0 {
		return gorm.ErrRecordNotFound
	}

	return nil
}

// IsTokenActive checks whether a token session exists.
func (r *Repository) IsTokenActive(token string) (bool, error) {
	session, err := r.GetSessionByToken(token)
	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return false, nil
		}
		return false, err
	}

	return session != nil, nil
}

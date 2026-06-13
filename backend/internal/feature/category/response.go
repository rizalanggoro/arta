package category

import "github.com/artafinance/backend/internal/dto"

// CreateCategoryRes response for create
type CreateCategoryRes struct {
	dto.Category
} // @name CreateCategoryRes

// GetCategoryRes response for get
type GetCategoryRes struct {
	Item dto.Category `json:"item"`
} // @name GetCategoryRes

// UpdateCategoryRes response for update
type UpdateCategoryRes struct {
	dto.Category
} // @name UpdateCategoryRes

// DeleteCategoryRes response for delete
type DeleteCategoryRes struct {
	Message string `json:"message"`
} // @name DeleteCategoryRes

// ListCategoriesRes response for list
type ListCategoriesRes struct {
	Categories []dto.Category `json:"categories"`
}

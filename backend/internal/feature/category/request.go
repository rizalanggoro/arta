package category

// CreateCategoryReq defines payload for creating a category.
type CreateCategoryReq struct {
	Name string `json:"name"`
	Type string `json:"type"`
}

// UpdateCategoryReq defines payload for updating a category.
type UpdateCategoryReq struct {
	Name *string `json:"name,omitempty"`
	Type *string `json:"type,omitempty"`
}

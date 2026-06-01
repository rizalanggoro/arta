package gold

// CreateGoldReq defines payload for creating a gold entry.
type CreateGoldReq struct {
	WalletID uint    `json:"wallet_id"`
	Date     string  `json:"date"`
	Grams    float64 `json:"grams" format:"double"`
	Price    float64 `json:"price" format:"double"`
	Type     string  `json:"type"`
	Carat    float64 `json:"carat" format:"double"`
	Notes    string  `json:"notes"`
}

// UpdateGoldReq defines payload for updating a gold entry.
type UpdateGoldReq struct {
	Date  *string  `json:"date,omitempty"`
	Grams *float64 `json:"grams,omitempty"`
	Price *float64 `json:"price,omitempty"`
	Type  *string  `json:"type,omitempty"`
	Carat *float64 `json:"carat,omitempty"`
	Notes *string  `json:"notes,omitempty"`
}

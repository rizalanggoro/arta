package gold

import "time"

// CreateGoldReq defines payload for creating a gold entry.
type CreateGoldReq struct {
	WalletID      uint      `json:"wallet_id"`
	Date          time.Time `json:"date"`
	Grams         float64   `json:"grams"`
	PricePerGram  float64   `json:"price_per_gram"`
	Type          string    `json:"type"`
	PurityPercent float64   `json:"purity_percent,omitempty"`
	Notes         string    `json:"notes,omitempty"`
}

// UpdateGoldReq defines payload for updating a gold entry.
type UpdateGoldReq struct {
	Date          *time.Time `json:"date,omitempty"`
	Grams         *float64   `json:"grams,omitempty"`
	PricePerGram  *float64   `json:"price_per_gram,omitempty"`
	Type          *string    `json:"type,omitempty"`
	PurityPercent *float64   `json:"purity_percent,omitempty"`
	Notes         *string    `json:"notes,omitempty"`
}

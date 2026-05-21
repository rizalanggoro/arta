package gold

import "time"

// CreateGoldReq defines payload for creating a gold entry.
type CreateGoldReq struct {
	WalletID uint      `json:"wallet_id"`
	Date     time.Time `json:"date"`
	Grams    float64   `json:"grams"`
	// Price is the total purchase price for the recorded grams
	Price         float64 `json:"price"`
	Type          string  `json:"type"`
	Carat         float64 `json:"carat,omitempty"`
	Notes         string  `json:"notes,omitempty"`
}

// UpdateGoldReq defines payload for updating a gold entry.
type UpdateGoldReq struct {
	Date          *time.Time `json:"date,omitempty"`
	Grams         *float64   `json:"grams,omitempty"`
	Price         *float64   `json:"price,omitempty"`
	Type          *string    `json:"type,omitempty"`
	Carat         *float64   `json:"carat,omitempty"`
	Notes         *string    `json:"notes,omitempty"`
}

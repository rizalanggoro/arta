package wallet

// CreateWalletReq defines payload for creating a wallet.
type CreateWalletReq struct {
	Name      string `json:"name"`
	Type      string `json:"type"`
	IsDefault bool   `json:"is_default"`
}

// UpdateWalletReq defines payload for updating a wallet.
type UpdateWalletReq struct {
	Name      *string `json:"name"`
	Type      *string `json:"type"`
	IsDefault *bool   `json:"is_default"`
}

package wallet

import "github.com/artafinance/backend/internal/dto"

// CreateWalletRes represents response for create wallet
type CreateWalletRes struct {
	dto.Wallet
} // @name CreateWalletRes

// GetWalletRes represents response for get wallet
type GetWalletRes struct {
	dto.Wallet
} // @name GetWalletRes

// UpdateWalletRes represents response for update wallet
type UpdateWalletRes struct {
	dto.Wallet
} // @name UpdateWalletRes

// DeleteWalletRes represents response for delete wallet
type DeleteWalletRes struct {
	Message string `json:"message"`
} // @name DeleteWalletRes

// ListWalletsRes represents list response.
type ListWalletsRes struct {
	Wallets []dto.Wallet `json:"wallets"`
}

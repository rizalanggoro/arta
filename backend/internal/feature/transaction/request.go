package transaction

// CreateTransactionReq defines payload for creating a transaction.
type CreateTransactionReq struct {
	WalletID    uint    `json:"wallet_id"`
	Amount      float64 `json:"amount"`
	CategoryID  uint    `json:"category_id"`
	Description string  `json:"description,omitempty"`
	Date        string  `json:"date"`
} // @name CreateTransactionReq

// UpdateTransactionReq defines payload for updating a transaction.
type UpdateTransactionReq struct {
	WalletID    *uint    `json:"wallet_id,omitempty"`
	Amount      *float64 `json:"amount,omitempty"`
	CategoryID  *uint    `json:"category_id,omitempty"`
	Description *string  `json:"description,omitempty"`
	Date        *string  `json:"date,omitempty"`
} // @name UpdateTransactionReq

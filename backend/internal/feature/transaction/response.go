package transaction

import "github.com/artafinance/backend/internal/dto"

// CreateTransactionRes response for create
type CreateTransactionRes struct {
	dto.Transaction
} // @name CreateTransactionRes

// GetTransactionRes response for get
type GetTransactionRes struct {
	dto.Transaction
} // @name GetTransactionRes

// UpdateTransactionRes response for update
type UpdateTransactionRes struct {
	dto.Transaction
} // @name UpdateTransactionRes

// DeleteTransactionRes response for delete
type DeleteTransactionRes struct {
	Message string `json:"message"`
} // @name DeleteTransactionRes

// ListTransactionsRes response for list
type ListTransactionsRes struct {
	Transactions []dto.Transaction `json:"transactions"`
} // @name ListTransactionsRes

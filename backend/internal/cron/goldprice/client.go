package goldprice

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	"github.com/artafinance/backend/internal/domain"
)

const defaultGoldAPIURL = "https://api.gold-api.com/price/XAU"

// Client fetches gold prices from the public API.
type Client struct {
	httpClient *http.Client
	apiURL     string
}

// NewClient creates a new gold price API client.
func NewClient() *Client {
	return &Client{
		httpClient: &http.Client{Timeout: 15 * time.Second},
		apiURL:     defaultGoldAPIURL,
	}
}

type apiResponse struct {
	Currency          string  `json:"currency"`
	CurrencySymbol    string  `json:"currencySymbol"`
	ExchangeRate      float64 `json:"exchangeRate"`
	Name              string  `json:"name"`
	Price             float64 `json:"price"`
	Symbol            string  `json:"symbol"`
	UpdatedAt         string  `json:"updatedAt"`
	UpdatedAtReadable string  `json:"updatedAtReadable"`
}

// FetchLatest gets the latest price snapshot from the public API.
func (c *Client) FetchLatest(ctx context.Context) (*domain.GoldPrice, error) {
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, c.apiURL, nil)
	if err != nil {
		return nil, err
	}

	res, err := c.httpClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer res.Body.Close()

	if res.StatusCode < 200 || res.StatusCode >= 300 {
		return nil, fmt.Errorf("gold api returned status %d", res.StatusCode)
	}

	var payload apiResponse
	if err := json.NewDecoder(res.Body).Decode(&payload); err != nil {
		return nil, err
	}

	updatedAt, err := time.Parse(time.RFC3339, payload.UpdatedAt)
	if err != nil {
		return nil, err
	}

	return &domain.GoldPrice{
		Symbol:            payload.Symbol,
		Currency:          payload.Currency,
		CurrencySymbol:    payload.CurrencySymbol,
		ExchangeRate:      payload.ExchangeRate,
		PricePerOunceUSD:  payload.Price,
		SourceUpdatedAt:   updatedAt,
		SourceReadableAge: payload.UpdatedAtReadable,
	}, nil
}

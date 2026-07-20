package fxrate

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	"github.com/artafinance/backend/internal/domain"
)

const defaultFrankfurterURL = "https://api.frankfurter.dev/v1/latest?base=USD&symbols=IDR"

// Client fetches FX rates from the public API.
type Client struct {
	httpClient *http.Client
	apiURL     string
}

// NewClient creates a new FX rate API client.
func NewClient() *Client {
	return &Client{
		httpClient: &http.Client{Timeout: 15 * time.Second},
		apiURL:     defaultFrankfurterURL,
	}
}

type apiResponse struct {
	Amount float64          `json:"amount" format:"double"`
	Base   string           `json:"base"`
	Date   string           `json:"date"`
	Rates  map[string]int64 `json:"rates"`
}

// FetchLatest gets the latest FX snapshot from the public API.
func (c *Client) FetchLatest(ctx context.Context) (*domain.FxRate, error) {
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
		return nil, fmt.Errorf("fx api returned status %d", res.StatusCode)
	}

	var payload apiResponse
	if err := json.NewDecoder(res.Body).Decode(&payload); err != nil {
		return nil, err
	}

	parsedDate, err := time.Parse("2006-01-02", payload.Date)
	if err != nil {
		return nil, err
	}

	rate, ok := payload.Rates["IDR"]
	if !ok {
		return nil, fmt.Errorf("fx api response missing IDR rate")
	}

	return &domain.FxRate{
		Base: payload.Base,
		Date: parsedDate,
		Rate: int(rate),
	}, nil
}

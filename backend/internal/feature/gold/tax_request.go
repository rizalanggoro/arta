package gold

// GoldTaxPreferenceReq represents a single gold tax preference payload.
type GoldTaxPreferenceReq struct {
	Carat   float64 `json:"carat" format:"double"`
	TaxRate float64 `json:"tax_rate" format:"double"`
}

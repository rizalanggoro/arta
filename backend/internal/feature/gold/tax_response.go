package gold

import "github.com/artafinance/backend/internal/dto"

// ListGoldTaxPreferencesRes response for listing gold tax preferences.
type ListGoldTaxPreferencesRes struct {
	Preferences []dto.GoldTaxPreference `json:"preferences"`
} // @name ListGoldTaxPreferencesRes

// CreateGoldTaxPreferenceRes response for creating a gold tax preference.
type CreateGoldTaxPreferenceRes struct {
	Preference dto.GoldTaxPreference `json:"preference"`
} // @name CreateGoldTaxPreferenceRes

// UpdateGoldTaxPreferenceRes response for updating a gold tax preference.
type UpdateGoldTaxPreferenceRes struct {
	Preference dto.GoldTaxPreference `json:"preference"`
} // @name UpdateGoldTaxPreferenceRes

// DeleteGoldTaxPreferenceRes response for deleting a gold tax preference.
type DeleteGoldTaxPreferenceRes struct {
	Message string `json:"message"`
} // @name DeleteGoldTaxPreferenceRes

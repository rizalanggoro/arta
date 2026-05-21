package id.my.rizalanggoro.arta.feature.gold.data.mapper

import id.my.rizalanggoro.arta.domain.GoldTaxPreference
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldTaxPreferenceDto
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldTaxPreferenceRequestDto

fun GoldTaxPreferenceDto.toTaxDomain(): GoldTaxPreference {
	return GoldTaxPreference(
		id = id,
		userId = userId,
		carat = carat,
		taxRate = taxRate,
		createdAt = createdAt,
		updatedAt = updatedAt,
	)
}

fun GoldTaxPreference.toTaxPreferenceRequestDto(): GoldTaxPreferenceRequestDto {
	return GoldTaxPreferenceRequestDto(
		carat = carat,
		taxRate = taxRate,
	)
}
package id.my.rizalanggoro.arta.feature.gold.data.mapper

import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldDto

fun GoldDto.toDomain(): Gold {
	return Gold(
		id = id,
		walletId = walletId,
		date = date,
		grams = grams,
		price = price,
		type = type,
		carat = carat,
		notes = notes,
		createdAt = createdAt,
		updatedAt = updatedAt,
	)
}

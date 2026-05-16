package id.my.rizalanggoro.arta.feature.gold.data.mapper

import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.feature.gold.data.dto.GoldDto

fun GoldDto.toDomain(): Gold {
	return Gold(
		id = id,
		walletId = walletId,
		date = date,
		grams = grams,
		pricePerGram = pricePerGram,
		totalValue = totalValue,
		type = type,
		purityPercent = purityPercent,
		notes = notes,
		createdAt = createdAt,
		updatedAt = updatedAt,
	)
}

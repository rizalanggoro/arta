package id.my.rizalanggoro.arta.feature.wallet.data.mapper

import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.feature.wallet.data.dto.WalletDto

fun WalletDto.toDomain(): Wallet {
	return Wallet(
		id = id,
		userId = userId,
		name = name,
		type = type,
		createdAt = createdAt,
		updatedAt = updatedAt,
	)
}

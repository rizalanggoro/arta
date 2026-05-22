package id.my.rizalanggoro.arta.feature.home.data.mapper

import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.domain.GoldDashboard
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardGoldDataDto
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardGoldDto
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardResponseDto

fun GoldDashboardResponseDto.toDomain(): GoldDashboard {
    return GoldDashboard(
        activeWalletName = activeWalletName,
        totalAsset = totalAsset,
        buyPrice = buyPrice,
        profit = profit,
        totalWeight = totalWeight,
        totalGoldItems = totalGoldItems,
        latestDollarPrice = latestDollarPrice,
        latestGoldPricePerGramIdr = latestGoldPricePerGramIdr,
        recentGolds = recentGolds.map { it.toDomain() },
    )
}

fun GoldDashboardGoldDto.toDomain(): Gold {
    return data.toDomain().copy(
        sellPrice = sellPrice,
        profit = profit,
    )
}

fun GoldDashboardGoldDataDto.toDomain(): Gold {
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

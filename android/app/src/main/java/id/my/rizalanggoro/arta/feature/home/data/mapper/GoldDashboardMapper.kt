package id.my.rizalanggoro.arta.feature.home.data.mapper

import id.my.rizalanggoro.arta.domain.GoldDashboardGold
import id.my.rizalanggoro.arta.domain.GoldDashboardOverview
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardGoldDto
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardGoldDataDto
import id.my.rizalanggoro.arta.feature.home.data.dto.GoldDashboardResponseDto

fun GoldDashboardResponseDto.toDomain(): GoldDashboardOverview {
    return GoldDashboardOverview(
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

fun GoldDashboardGoldDto.toDomain(): GoldDashboardGold {
    return GoldDashboardGold(
        id = data.id,
        walletId = data.walletId,
        date = data.date,
        grams = data.grams,
        price = data.price,
        type = data.type,
        purityPercent = data.purityPercent,
        notes = data.notes,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt,
    )
}

fun GoldDashboardGoldDataDto.toDomain(): GoldDashboardGold {
    return GoldDashboardGold(
        id = id,
        walletId = walletId,
        date = date,
        grams = grams,
        price = price,
        type = type,
        purityPercent = purityPercent,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
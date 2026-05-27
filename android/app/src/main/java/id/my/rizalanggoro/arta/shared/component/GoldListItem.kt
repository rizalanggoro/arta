package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingFlat
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.core.helper.getBottomRadius
import id.my.rizalanggoro.arta.core.helper.getTopRadius
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold

@Composable
fun GoldListItem(
    index: Int = 0,
    size: Int = 1,
    gold: DtoGold,
    onClick: () -> Unit = {}
) {
    val status = when {
        gold.profit > 0.toBigDecimal() -> 1
        gold.profit < 0.toBigDecimal() -> -1
        else -> 0
    }
    val icons = listOf(
        Icons.AutoMirrored.Rounded.TrendingDown,
        Icons.AutoMirrored.Rounded.TrendingFlat,
        Icons.AutoMirrored.Rounded.TrendingUp
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = getTopRadius(index, size),
                    topEnd = getTopRadius(index, size),
                    bottomStart = getBottomRadius(index, size),
                    bottomEnd = getBottomRadius(index, size),
                )
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            status in 0..1 -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icons[status + 1],
                    null,
                    tint = when {
                        status in 0..1 -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = gold.data.date.toIndonesianDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = gold.sellPrice.toIndonesianCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (status != 0)
                    Text(
                        text = "${
                            when {
                                status == 1 -> "Keuntungan"
                                else -> "Kerugian"
                            }
                        } ${gold.profit.toIndonesianCurrency()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
            }
        }
        Text(
            text = "${gold.data.grams.toFloat()} gr",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

private val golds = List(3) {
    DtoGold(
        data = DomainGold(
            carat = 24.toBigDecimal(),
            createdAt = "2026-05-25T14:38:00.000+07:00",
            date = "2026-05-25T14:38:00.000+07:00",
            grams = 3.3.toBigDecimal(),
            id = 1,
            notes = "",
            price = 1500000.toBigDecimal(),
            type = "jewelry",
            updatedAt = "2026-05-25T14:38:00.000+07:00",
            walletId = 1
        ),
        profit = ((it - 1) * 500000).toBigDecimal(),
        sellPrice = (1500000 + ((it - 1) * 500000)).toBigDecimal(),
    )
}

@Composable
@Preview
private fun DownPreview() {
    GoldListItem(
        gold = golds[0]
    )
}

@Composable
@Preview
private fun FlatPreview() {
    GoldListItem(
        gold = golds[1]
    )
}

@Composable
@Preview
private fun UpPreview() {
    GoldListItem(
        gold = golds[2]
    )
}
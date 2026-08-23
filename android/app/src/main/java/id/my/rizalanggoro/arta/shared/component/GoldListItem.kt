package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingFlat
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun GoldListItem(
    gold: DtoGold,
    onClick: (DomainGold) -> Unit = {},
    onLongClick: (DomainGold) -> Unit = {},
) {
    val status = when {
        gold.profit > 0 -> 1
        gold.profit < 0 -> -1
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
            .combinedClickable(
                onClick = { onClick(gold.data) },
                onLongClick = { onLongClick(gold.data) }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
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
                            status in 0..1 -> MiuixTheme.colorScheme.primary
                            else -> MiuixTheme.colorScheme.error
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icons[status + 1],
                    null,
                    tint = when {
                        status in 0..1 -> MiuixTheme.colorScheme.onPrimary
                        else -> MiuixTheme.colorScheme.onError
                    }
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = gold.data.date.toIndonesianDate(),
                    style = MiuixTheme.textStyles.footnote1,
                    color = MiuixTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = gold.sellPrice.toIndonesianCurrency(),
                    style = MiuixTheme.textStyles.body1,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Text(
                        text = "Harga beli ${gold.data.price.toIndonesianCurrency()}",
                        style = MiuixTheme.textStyles.footnote2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    if (status != 0)
                        Text(
                            text = "${
                                when {
                                    status == 1 -> "Keuntungan"
                                    else -> "Kerugian"
                                }
                            } ${gold.profit.toIndonesianCurrency()}",
                            style = MiuixTheme.textStyles.footnote2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "${gold.data.carat.toInt()}k",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${gold.data.grams.toFloat()}gr",
                style = MiuixTheme.textStyles.footnote2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}

private val golds = List(3) {
    DtoGold(
        data = DomainGold(
            carat = 24.0,
            createdAt = "2026-05-25T14:38:00.000+07:00",
            date = "2026-05-25T14:38:00.000+07:00",
            grams = 3.3,
            id = 1,
            notes = "",
            price = 1500000.0,
            type = "jewelry",
            updatedAt = "2026-05-25T14:38:00.000+07:00",
            walletId = 1
        ),
        profit = ((it - 1) * 500000.0),
        sellPrice = (1500000 + ((it - 1) * 500000.0)),
    )
}

@Composable
@Preview
private fun DownPreview() {
    MiuixTheme {
        GoldListItem(
            gold = golds[0]
        )
    }
}

@Composable
@Preview
private fun FlatPreview() {
    MiuixTheme {
        GoldListItem(
            gold = golds[1]
        )
    }
}

@Composable
@Preview
private fun UpPreview() {
    MiuixTheme {
        GoldListItem(
            gold = golds[2]
        )
    }
}

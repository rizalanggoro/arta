package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.GoldListItem

@Composable
fun LatestGold(
    golds: List<DtoGold> = emptyList(),
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(
                    top = 16.dp,
                    bottom = (32 + 56).dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Emas Terbaru",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            when {
                golds.isEmpty() -> EmptyPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                )

                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        golds.forEachIndexed { index, gold ->
                            GoldListItem(
                                gold = gold,
                                onClick = {},
                                index = index,
                                size = golds.size
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(device = "id:pixel_10_pro_xl")
private fun Preview() {
    LatestGold(
        golds = List(3) {
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
    )
}

@Composable
@Preview
private fun EmptyPreview() {
    LatestGold(
        golds = emptyList()
    )
}
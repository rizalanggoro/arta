package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.GoldListItem

@Composable
fun LatestGold(
    golds: List<DtoGold> = emptyList(),
    onClickShowMore: () -> Unit = {}
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
                        golds.forEach { gold ->
                            GoldListItem(
                                gold = gold,
                                onClick = {}
                            )
                        }
                    }

                    Button(
                        onClick = onClickShowMore,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Lihat lainnya")
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowForward,
                                null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
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
package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.GoldListItem
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun LatestGold(
    golds: List<DtoGold> = emptyList(),
    onLongClickGold: (DomainGold) -> Unit = {},
) {
    Card(
        colors = CardDefaults.defaultColors(
            color = MiuixTheme.colorScheme.background
        ),
        modifier = Modifier
            .background(MiuixTheme.colorScheme.background)
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
                fontSize = 16.sp,
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
                                onLongClick = onLongClickGold,
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
@Preview
private fun Preview() {
    ArtaMiuixTheme {
        LatestGold(
            golds = List(3) {
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
        )
    }
}

@Composable
@Preview
private fun EmptyPreview() {
    ArtaMiuixTheme {
        LatestGold(
            golds = emptyList()
        )
    }
}

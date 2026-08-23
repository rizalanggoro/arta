package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.GoldListItem
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle

@Composable
fun LatestGold(
    golds: List<DtoGold> = emptyList(),
    onLongClickGold: (DomainGold) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SmallTitle(
            text = "Emas Terbaru",
            insideMargin = PaddingValues(top = 8.dp),
        )

            when {
                golds.isEmpty() -> EmptyPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )

                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        golds.forEachIndexed { index, gold ->
                            GoldListItem(
                                gold = gold,
                                onLongClick = onLongClickGold,
                            )
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

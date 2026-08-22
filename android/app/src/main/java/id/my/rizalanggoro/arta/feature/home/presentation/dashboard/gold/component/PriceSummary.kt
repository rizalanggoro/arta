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
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.openapi.models.DomainGoldTaxPreference
import id.my.rizalanggoro.arta.openapi.models.DtoGoldTax
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun PriceSummary(
    retailPrice: Double = 0.0,
    goldTaxes: List<DtoGoldTax> = emptyList(),
    onClickManageTax: () -> Unit = {},
) {
    Card(
        cornerRadius = 24.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.defaultColors(
            color = MiuixTheme.colorScheme.background
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Ringkasan Harga",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = onClickManageTax) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.EditNote,
                            null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("Atur pajak")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MiuixTheme.colorScheme.surfaceContainer)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        retailPrice.toIndonesianCurrency(),
                        fontSize = 16.sp
                    )
                    Text(
                        "Harga emas/gram (sebelum pajak)",
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }

                // price per carat
                if (goldTaxes.isNotEmpty()) {
                    Text(
                        "Berikut harga emas/gram untuk setiap karat setelah perhitungan konfigurasi pajak",
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        goldTaxes.forEach {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MiuixTheme.colorScheme.surfaceContainer)
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        it.sellPrice.toIndonesianCurrency(),
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        "Besaran pajak ${it.data.taxRate}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                    )
                                }
                                Text(
                                    "${it.data.carat.toInt()}k",
                                    fontSize = 12.sp,
                                    color = MiuixTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
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
        PriceSummary(
            retailPrice = 2800000.0,
            goldTaxes = listOf(
                DtoGoldTax(
                    data = DomainGoldTaxPreference(
                        carat = 17.0,
                        createdAt = "",
                        id = 1,
                        taxRate = 5.0,
                        updatedAt = "",
                        userId = 1,
                    ),
                    sellPrice = 1200000.0
                ),
            )
        )
    }
}

@Composable
@Preview
private fun EmptyPreview() {
    ArtaMiuixTheme {
        PriceSummary()
    }
}

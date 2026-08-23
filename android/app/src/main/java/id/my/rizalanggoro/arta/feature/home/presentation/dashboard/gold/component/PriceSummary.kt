package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun PriceSummary(
    retailPrice: Double = 0.0,
    goldTaxes: List<DtoGoldTax> = emptyList(),
    onClickManageTax: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SmallTitle(
                text = "Ringkasan Harga",
                modifier = Modifier.weight(1f),
                insideMargin = PaddingValues(vertical = 8.dp),
            )

            IconButton(onClick = onClickManageTax) {
                Icon(
                    MiuixIcons.Settings,
                    null
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            BasicComponent(
                title = retailPrice.toIndonesianCurrency(),
                summary = "Harga emas/gram (sebelum pajak)",
            )
        }

        // price per carat
        if (goldTaxes.isNotEmpty()) {
            Text(
                "Berikut harga emas/gram untuk setiap karat setelah perhitungan konfigurasi pajak",
                style = MiuixTheme.textStyles.footnote2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                goldTaxes.forEach {
                    BasicComponent(
                        title = it.sellPrice.toIndonesianCurrency(),
                        summary = "Besaran pajak ${it.data.taxRate}%",
                        endActions = {
                            Text(
                                "${it.data.carat.toInt()}k",
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                    )
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

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import id.my.rizalanggoro.arta.openapi.models.DomainGoldTaxPreference
import id.my.rizalanggoro.arta.openapi.models.DtoGoldTax

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PriceSummary(
    retailPrice: Double = 0.0,
    goldTaxes: List<DtoGoldTax> = emptyList(),
    onClickManageTax: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = onClickManageTax) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ButtonDefaults.IconSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.EditNote,
                            null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Text("Atur pajak")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        retailPrice.toIndonesianCurrency(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Harga emas/gram (sebelum pajak)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // price per carat
                if (goldTaxes.isNotEmpty()) {
                    Text(
                        "Berikut harga emas/gram untuk setiap karat setelah perhitungan konfigurasi pajak",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
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
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        it.sellPrice.toIndonesianCurrency(),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    Text(
                                        "Besaran pajak ${it.data.taxRate}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    "${it.data.carat.toInt()}k",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
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

@Composable
@Preview
private fun EmptyPreview() {
    PriceSummary()
}
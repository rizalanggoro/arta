package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.component

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingFlat
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency

@Composable
fun IncomeExpenseSummary(
    modifier: Modifier = Modifier,
    totalIncome: Double = 0.0,
    totalExpense: Double = 0.0,
    prevPeriodIncome: Double = 0.0,
    prevPeriodExpense: Double = 0.0,
    isLoading: Boolean = false,
) {
    val totalDifference = totalIncome - totalExpense
    val prevNet = prevPeriodIncome - prevPeriodExpense
    val changePercent = remember(totalDifference, prevNet) {
        if (prevNet != 0.0) {
            ((totalDifference - prevNet) / kotlin.math.abs(prevNet) * 100).toInt()
        } else null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),

            ) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Pemasukan",
                value = totalIncome,
                icon = Icons.AutoMirrored.Rounded.CallReceived,
                isLoading = isLoading,
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Pengeluaran",
                value = totalExpense,
                icon = Icons.AutoMirrored.Rounded.CallMade,
                isLoading = isLoading,
            )
        }

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .then(
                    when {
                        isLoading -> Modifier.shimmer()
                        else -> Modifier
                    }
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isLoading -> MaterialTheme.colorScheme.outlineVariant
                            else -> MaterialTheme.colorScheme.primary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!isLoading)
                    Icon(
                        when {
                            changePercent == null || changePercent == 0 -> Icons.AutoMirrored.Rounded.TrendingFlat
                            changePercent > 0 -> Icons.AutoMirrored.Rounded.TrendingUp
                            else -> Icons.AutoMirrored.Rounded.TrendingDown
                        },
                        null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    when {
                        isLoading -> 2.dp
                        else -> 0.dp
                    }
                )
            ) {
                Text(
                    totalDifference.toIndonesianCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        isLoading -> Color.Transparent
                        else -> Color.Unspecified
                    },
                    modifier = Modifier.then(
                        when {
                            isLoading -> Modifier
                                .fillMaxWidth(.5f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant)

                            else -> Modifier
                        }
                    )
                )
                Text(
                    when {
                        isLoading -> ""
                        changePercent == null -> "-"
                        changePercent > 0 -> "+$changePercent% dari periode sebelumnya"
                        changePercent < 0 -> "$changePercent% dari periode sebelumnya"
                        else -> "Tidak ada perubahan"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isLoading -> Color.Transparent
                        else -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .8f)
                    },
                    modifier = Modifier.then(
                        when {
                            isLoading -> Modifier
                                .fillMaxWidth(.8f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant)

                            else -> Modifier
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String = "Pemasukan",
    value: Double = 0.0,
    icon: ImageVector = Icons.AutoMirrored.Rounded.CallReceived,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .then(
                when {
                    isLoading -> Modifier.shimmer()
                    else -> Modifier
                }
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    icon,
                    null,
                    tint = when {
                        isLoading -> Color.Transparent
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier
                        .size(14.dp)
                        .then(
                            when {
                                isLoading -> Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outlineVariant)

                                else -> Modifier
                            }
                        )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isLoading -> Color.Transparent
                        else -> MaterialTheme.colorScheme.primary
                    },
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.then(
                        when {
                            isLoading -> Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant)

                            else -> Modifier
                        }
                    )
                )
            }
            Text(
                text = value.toIndonesianCurrency(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isLoading -> Color.Transparent
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier.then(
                    when {
                        isLoading -> Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)

                        else -> Modifier
                    }
                )
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    IncomeExpenseSummary()
}
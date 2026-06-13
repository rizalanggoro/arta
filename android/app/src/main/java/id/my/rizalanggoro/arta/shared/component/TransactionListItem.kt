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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.getBottomRadius
import id.my.rizalanggoro.arta.core.utils.getTopRadius
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.openapi.models.DtoTransaction

@Composable
fun TransactionListItem(
    modifier: Modifier = Modifier,
    transaction: DtoTransaction,
    index: Int = 0,
    size: Int = 1,
    onClick: (DomainTransaction) -> Unit = {},
    onLongClick: (DomainTransaction) -> Unit = {},
    isLoading: Boolean = false,
) {
    Row(
        modifier = modifier
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
            .combinedClickable(
                onClick = { onClick(transaction.data) },
                onLongClick = { onLongClick(transaction.data) }
            )
            .padding(16.dp)
            .then(
                when {
                    isLoading -> Modifier.shimmer()
                    else -> Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .background(
                    when {
                        isLoading -> MaterialTheme.colorScheme.outlineVariant
                        else -> when {
                            transaction.category.type == "income" -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.errorContainer
                        }
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (!isLoading)
                Icon(
                    when {
                        transaction.category.type == "income" -> Icons.AutoMirrored.Rounded.CallReceived
                        else -> Icons.AutoMirrored.Rounded.CallMade
                    },
                    null,
                    tint = when {
                        transaction.category.type == "income" -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(
                when {
                    isLoading -> 4.dp
                    else -> 0.dp
                }
            )
        ) {
            Text(
                transaction.data.amount.toIndonesianCurrency(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.then(
                    when {
                        isLoading -> Modifier
                            .shimmer()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)

                        else -> Modifier
                    }
                ),
                color = when {
                    isLoading -> Color.Transparent
                    else -> Color.Unspecified
                }
            )
            Text(
                transaction.category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isLoading -> Color.Transparent
                    else -> MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.then(
                    when {
                        isLoading -> Modifier
                            .shimmer()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)

                        else -> Modifier
                    }
                )
            )
        }
        Text(
            transaction.data.date.toFormattedDate("E, dd/M/yy"),
            style = MaterialTheme.typography.labelSmall,
            color = when {
                isLoading -> Color.Transparent
                else -> MaterialTheme.colorScheme.outline
            },
            fontWeight = FontWeight.Normal,
            modifier = Modifier.then(
                when {
                    isLoading -> Modifier
                        .shimmer()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant)

                    else -> Modifier
                }
            )
        )
    }
}

@Composable
@Preview
private fun IncomePreview() {
    TransactionListItem(
        transaction = DtoTransaction(
            data = DomainTransaction(
                amount = 1500000.0,
                categoryId = 1,
                createdAt = "",
                date = "2024-05-28T14:30:00+05:30",
                description = "",
                id = 1,
                updatedAt = "",
                walletId = 1
            ),
            category = DomainCategory(
                createdAt = "",
                id = 1,
                name = "Uang saku bulanan",
                type = "income",
                updatedAt = "",
                userId = 1
            )
        ),
        isLoading = true
    )
}
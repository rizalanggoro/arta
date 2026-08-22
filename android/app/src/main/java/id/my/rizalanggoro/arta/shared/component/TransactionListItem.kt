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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.Samples
import id.my.rizalanggoro.arta.core.utils.getBottomRadius
import id.my.rizalanggoro.arta.core.utils.getTopRadius
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun TransactionListItem(
    modifier: Modifier = Modifier,
    transaction: DomainTransaction,
    category: DomainCategory,
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
            .background(MiuixTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                onClick = { onClick(transaction) },
                onLongClick = { onLongClick(transaction) }
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
                        isLoading -> MiuixTheme.colorScheme.outline
                        else -> when {
                            category.type == "income" -> MiuixTheme.colorScheme.primary
                            else -> MiuixTheme.colorScheme.error
                        }
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (!isLoading)
                Icon(
                    when {
                        category.type == "income" -> Icons.AutoMirrored.Rounded.CallReceived
                        else -> Icons.AutoMirrored.Rounded.CallMade
                    },
                    null,
                    tint = when {
                        category.type == "income" -> MiuixTheme.colorScheme.onPrimary
                        else -> MiuixTheme.colorScheme.onError
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
                transaction.amount.toIndonesianCurrency(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.then(
                    when {
                        isLoading -> Modifier
                            .shimmer()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MiuixTheme.colorScheme.outline)

                        else -> Modifier
                    }
                ),
                color = when {
                    isLoading -> Color.Transparent
                    else -> Color.Unspecified
                }
            )
            Text(
                category.name,
                fontSize = 14.sp,
                color = when {
                    isLoading -> Color.Transparent
                    else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                },
                modifier = Modifier.then(
                    when {
                        isLoading -> Modifier
                            .shimmer()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MiuixTheme.colorScheme.outline)

                        else -> Modifier
                    }
                )
            )
        }
        Text(
            transaction.date.toFormattedDate("E, dd/M/yy"),
            fontSize = 11.sp,
            color = when {
                isLoading -> Color.Transparent
                else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
            },
            fontWeight = FontWeight.Normal,
            modifier = Modifier.then(
                when {
                    isLoading -> Modifier
                        .shimmer()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MiuixTheme.colorScheme.outline)

                    else -> Modifier
                }
            )
        )
    }
}

@Composable
@Preview
private fun IncomePreview() {
    ArtaMiuixTheme {
        TransactionListItem(
            transaction = Samples.domainTransactions.first(),
            category = Samples.domainCategories.first(),
            isLoading = true
        )
    }
}

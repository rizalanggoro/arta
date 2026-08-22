package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.getBottomRadius
import id.my.rizalanggoro.arta.core.utils.getTopRadius
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun DashboardCategoryListItem(
    modifier: Modifier = Modifier,
    category: DtoCategory,
    index: Int = 0,
    size: Int = 1,
    onClick: (DomainCategory) -> Unit = {},
    isLoading: Boolean = false,
) {
    Row(
        modifier = modifier
            .then(
                when {
                    isLoading -> Modifier.shimmer()
                    else -> Modifier
                }
            )
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
            .clickable { onClick(category.data) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    when {
                        isLoading -> MiuixTheme.colorScheme.outline
                        else -> when (category.data.type) {
                            "income" -> MiuixTheme.colorScheme.primary
                            else -> MiuixTheme.colorScheme.error
                        }
                    }
                )
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (!isLoading)
                Icon(
                    when (category.data.type) {
                        "income" -> Icons.AutoMirrored.Rounded.CallReceived
                        else -> Icons.AutoMirrored.Rounded.CallMade
                    },
                    null,
                    tint = when (category.data.type) {
                        "income" -> MiuixTheme.colorScheme.onPrimary
                        else -> MiuixTheme.colorScheme.onError
                    }
                )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(
                when {
                    isLoading -> 2.dp
                    else -> 0.dp
                }
            )
        ) {
            Text(
                (category.totalAmount ?: 0.0).toIndonesianCurrency(),
                fontSize = 16.sp,
                color = when {
                    isLoading -> Color.Transparent
                    else -> Color.Unspecified
                },
                modifier = Modifier.then(
                    when {
                        isLoading -> Modifier
                            .shimmer()
                            .fillMaxWidth(.5f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MiuixTheme.colorScheme.outline)

                        else -> Modifier
                    }
                )
            )
            Text(
                category.data.name,
                fontSize = 14.sp,
                color = when {
                    isLoading -> Color.Transparent
                    else -> MiuixTheme.colorScheme.onSurfaceVariantSummary
                },
                modifier = Modifier.then(
                    when {
                        isLoading -> Modifier
                            .shimmer()
                            .fillMaxWidth(.8f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MiuixTheme.colorScheme.outline)

                        else -> Modifier
                    }
                )
            )
        }
        Text(
            "${category.transactionCount ?: 0} trx",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                isLoading -> Color.Transparent
                else -> MiuixTheme.colorScheme.primary
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
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    ArtaMiuixTheme {
        DashboardCategoryListItem(
            category = DtoCategory(
                data = DomainCategory(
                    createdAt = "2024-06-01T00:00:00Z",
                    id = 1,
                    name = "Makanan dan minuman",
                    type = "expense",
                    updatedAt = "2024-06-01T00:00:00Z",
                    userId = 1
                ),
                totalAmount = 125500.0,
                transactionCount = 3,
                transactions = emptyList()
            )
        )
    }
}

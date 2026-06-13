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
import id.my.rizalanggoro.arta.core.utils.getBottomRadius
import id.my.rizalanggoro.arta.core.utils.getTopRadius
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory

@Composable
fun DashboardCategoryListItem(
    modifier: Modifier = Modifier,
    category: DtoCategory,
    index: Int = 0,
    size: Int = 1,
    onClick: (DomainCategory) -> Unit = {},
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
            .clickable { onClick(category.data) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    when (category.data.type) {
                        "income" -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                )
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when (category.data.type) {
                    "income" -> Icons.AutoMirrored.Rounded.CallReceived
                    else -> Icons.AutoMirrored.Rounded.CallMade
                },
                null,
                tint = when (category.data.type) {
                    "income" -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                category.totalAmount.toIndonesianCurrency(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                category.data.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Text(
            "${category.transactionCount} trx",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
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
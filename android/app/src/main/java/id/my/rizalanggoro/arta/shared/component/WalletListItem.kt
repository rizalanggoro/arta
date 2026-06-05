package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.core.utils.getBottomRadius
import id.my.rizalanggoro.arta.core.utils.getTopRadius
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

@Composable
fun WalletListItem(
    modifier: Modifier = Modifier,
    wallet: DomainWallet,
    onLongClick: (DomainWallet) -> Unit = {},
    index: Int = 0,
    size: Int = 1,
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        headlineContent = {
            Text(wallet.name)
        },
        supportingContent = {
            Text(wallet.type.toWalletName())
        },
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
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongClick(wallet) },
            )
    )
}

@Composable
@Preview
private fun Preview() {
    WalletListItem(
        wallet = DomainWallet(
            id = 1,
            name = "Dompet Utama",
            createdAt = "2024-06-01T12:00:00Z",
            updatedAt = "2024-06-01T12:00:00Z",
            type = "cash_savings",
            userId = 1,
        )
    )
}
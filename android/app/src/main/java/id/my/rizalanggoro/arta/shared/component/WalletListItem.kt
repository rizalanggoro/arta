package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import top.yukonga.miuix.kmp.basic.BasicComponent

@Composable
fun WalletListItem(
    modifier: Modifier = Modifier,
    wallet: DomainWallet,
    onLongClick: (DomainWallet) -> Unit = {},
) {
    BasicComponent(
        title = wallet.name,
        summary = wallet.type.toWalletName(),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongClick(wallet) },
            ),
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

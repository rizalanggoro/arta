package id.my.rizalanggoro.arta.feature.wallet.presentation.list.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.openapi.models.DomainWallet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletActionBS(
    wallet: DomainWallet,
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Content(
            wallet = wallet,
            onClickEdit = onClickEdit,
            onClickDelete = onClickDelete,
        )
    }
}

@Composable
private fun Content(
    wallet: DomainWallet,
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = wallet.name.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = wallet.type.orEmpty().toWalletName(),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall
            )
        }

        HorizontalDivider()

        Column {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = {
                    Icon(
                        Icons.Rounded.Edit,
                        null
                    )
                },
                headlineContent = { Text("Ubah") },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
                modifier = Modifier.clickable(onClick = onClickEdit),
            )
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = {
                    Icon(
                        Icons.Rounded.Delete,
                        null
                    )
                },
                headlineContent = { Text("Hapus") },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
                modifier = Modifier.clickable(onClick = onClickDelete),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    Content(
        wallet = DomainWallet(
            id = 1,
            name = "Dompet Utama",
            type = "cash_savings"
            ,createdAt = "2026-05-23T00:00:00Z",
            updatedAt = "2026-05-23T00:00:00Z",
            userId = 1,
        )
    )
}
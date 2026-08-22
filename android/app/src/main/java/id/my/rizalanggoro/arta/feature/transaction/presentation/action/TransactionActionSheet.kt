package id.my.rizalanggoro.arta.feature.transaction.presentation.action

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ActionRow
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun TransactionActionSheet(
    transactionId: Int,
) {
    val backStack = LocalBackStack.current

    Content(
        onClickEdit = {
            backStack.apply {
                removeLastOrNull()
                add(
                    TransactionRoute.Upsert(
                        transactionId = transactionId
                    )
                )
            }
        },
        onClickDelete = {
            backStack.apply {
                removeLastOrNull()
                add(
                    TransactionRoute.Delete(
                        transactionId = transactionId
                    )
                )
            }
        },
        onClickCancel = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun Content(
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickCancel: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            ActionRow(
                title = "Ubah",
                icon = Icons.Rounded.Edit,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                ),
                onClick = onClickEdit
            )
            ActionRow(
                title = "Hapus",
                icon = Icons.Rounded.Delete,
                iconTint = MiuixTheme.colorScheme.error,
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 4.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp,
                ),
                onClick = onClickDelete
            )
        }
        Button(
            onClick = onClickCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Batal")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    ArtaMiuixTheme {
        Content()
    }
}

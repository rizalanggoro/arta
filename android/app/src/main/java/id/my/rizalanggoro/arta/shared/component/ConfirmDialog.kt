package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.window.WindowDialog

@Composable
fun ConfirmDialog(
    title: String = "",
    description: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirmRequest: () -> Unit = {},
    isLoading: Boolean = false,
    confirmText: String = "Oke",
    dismissText: String = "Batal",
) {
    WindowDialog(
        show = true,
        title = title,
        summary = description,
        onDismissRequest = if (isLoading) ({}) else onDismissRequest,
    ) {
        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                InfiniteProgressIndicator()
            }

            else -> Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(dismissText)
                }
                Button(
                    onClick = onConfirmRequest,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColorsPrimary()
                ) {
                    Text(confirmText)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    ArtaMiuixTheme {
        Box(Modifier.fillMaxWidth()) {
            ConfirmDialog(
                title = "Hapus",
                description = "Apakah anda yakin ingin menghapus item ini?",
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LoadingPreview() {
    ArtaMiuixTheme {
        Box(Modifier.fillMaxWidth()) {
            ConfirmDialog(
                title = "Hapus",
                description = "Apakah anda yakin ingin menghapus item ini?",
                isLoading = true,
            )
        }
    }
}

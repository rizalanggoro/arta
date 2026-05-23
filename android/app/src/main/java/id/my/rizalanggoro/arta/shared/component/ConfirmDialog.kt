package id.my.rizalanggoro.arta.shared.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    title: String = "",
    description: String = "",
    onDismissRequest: () -> Unit = {},
    onConfirmRequest: () -> Unit = {},
    isLoading: Boolean = false,
    confirmText: String = "Oke",
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            when {
                isLoading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }

                else -> Text(description)
            }
        },
        confirmButton = {
            if (!isLoading)
                TextButton(onClick = onConfirmRequest) {
                    Text(confirmText)
                }
        },
        dismissButton = {
            if (!isLoading)
                TextButton(onClick = onDismissRequest) {
                    Text("Batal")
                }
        }
    )
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun Preview() {
    ConfirmDialog(
        title = "Hapus",
        description = "Apakah anda yakin ingin menghapus item ini?",
    )
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
private fun LoadingPreview() {
    ConfirmDialog(
        title = "Hapus",
        description = "Apakah anda yakin ingin menghapus item ini?",
        isLoading = true,
    )
}
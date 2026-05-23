package id.my.rizalanggoro.arta.feature.gold.presentation.tax.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import id.my.rizalanggoro.arta.openapi.models.DtoGoldTaxPreference

@Composable
fun DeleteGoldTaxConfirmationDialog(
    preference: DtoGoldTaxPreference,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus preferensi karat?") },
        text = { Text("Karat ${preference.carat} dengan rasio pajak ${preference.taxRate}% akan dihapus.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
    )
}

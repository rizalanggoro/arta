package id.my.rizalanggoro.arta.shared.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MyDatePickerDialog(
    state: DatePickerState = rememberDatePickerState(),
    onDismiss: () -> Unit = {},
    onDateSelected: (Long?) -> Unit = {}
) {
    MaterialTheme(
        colorScheme = if (LocalIsDarkTheme.current) darkColorScheme() else lightColorScheme()
    ) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = { onDateSelected(state.selectedDateMillis) }) {
                    Text("Selesai")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
@Preview
private fun DatePickerDialogPreview() {
    MyDatePickerDialog { }
}

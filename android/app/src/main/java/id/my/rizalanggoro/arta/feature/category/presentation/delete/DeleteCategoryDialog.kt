package id.my.rizalanggoro.arta.feature.category.presentation.delete

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun DeleteCategoryDialog(
    vm: DeleteCategoryVM,
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.CategoryChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        uiState = uiState,
        onClickCancel = { backStack.removeLastOrNull() },
        onClickDelete = vm::delete
    )
}

@Composable
private fun Content(
    uiState: DeleteCategoryUiState = DeleteCategoryUiState(),
    onClickCancel: () -> Unit = {},
    onClickDelete: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = {
            if (!uiState.isLoading) onClickCancel()
        },
        title = {
            Text("Hapus")
        },
        text = {
            when {
                uiState.isLoading -> Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LoadingIndicator()
                }

                else -> Text(
                    "Apakah Anda yakin akan menghapus kategori yang dipilih? " +
                            "Tindakan ini tidak dapat dipulihkan"
                )
            }
        },
        dismissButton = {
            if (!uiState.isLoading)
                TextButton(onClick = onClickCancel) {
                    Text("Batal")
                }
        },
        confirmButton = {
            if (!uiState.isLoading)
                TextButton(onClick = onClickDelete) {
                    Text("Hapus")
                }
        }
    )
}

@Composable
@Preview
private fun Preview() {
    Content()
}

@Composable
@Preview
private fun LoadingPreview() {
    Content(
        uiState = DeleteCategoryUiState(
            isLoading = true
        )
    )
}

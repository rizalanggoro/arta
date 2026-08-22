package id.my.rizalanggoro.arta.feature.category.presentation.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
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
    ConfirmDialog(
        title = "Hapus",
        description = "Apakah Anda yakin akan menghapus kategori yang dipilih? " +
                "Tindakan ini tidak dapat dipulihkan",
        onDismissRequest = { if (!uiState.isLoading) onClickCancel() },
        onConfirmRequest = onClickDelete,
        isLoading = uiState.isLoading,
        confirmText = "Hapus",
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

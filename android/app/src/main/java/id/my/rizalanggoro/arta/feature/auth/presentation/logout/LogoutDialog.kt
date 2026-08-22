package id.my.rizalanggoro.arta.feature.auth.presentation.logout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.AuthRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun LogoutDialog(
    vm: LogoutVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<LogoutUiState.Event.LogoutSucceeded>()
            .collect {
                backStack.clear()
                backStack.add(AuthRoute.Login)
            }
    }

    Content(
        uiState = uiState,
        onClickCancel = { backStack.removeLastOrNull() },
        onClickLogout = vm::logout
    )
}

@Composable
private fun Content(
    uiState: LogoutUiState = LogoutUiState(),
    onClickCancel: () -> Unit = {},
    onClickLogout: () -> Unit = {},
) {
    ConfirmDialog(
        title = "Keluar",
        description = "Apakah Anda yakin akan keluar dari akun ini?",
        onDismissRequest = { if (!uiState.isLoading) onClickCancel() },
        onConfirmRequest = onClickLogout,
        isLoading = uiState.isLoading,
        confirmText = "Keluar",
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
        uiState = LogoutUiState(
            isLoading = true
        )
    )
}
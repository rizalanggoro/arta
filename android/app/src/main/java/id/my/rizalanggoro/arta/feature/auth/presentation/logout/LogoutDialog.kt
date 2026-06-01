package id.my.rizalanggoro.arta.feature.auth.presentation.logout

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
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.application.Routes
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
                backStack.add(Routes.LoginRoute)
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
    AlertDialog(
        onDismissRequest = {
            if (!uiState.isLoading) onClickCancel()
        },
        title = {
            Text("Keluar")
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
                    "Apakah Anda yakin akan keluar dari akun ini?"
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
                TextButton(onClick = onClickLogout) {
                    Text("Keluar")
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
        uiState = LogoutUiState(
            isLoading = true
        )
    )
}
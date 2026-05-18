package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeSettingScreen(
    vm: HomeSettingVM = viewModel(factory = HomeSettingVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()

    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        vm.event.collect {
            when (it) {
                is HomeSettingEvent.LogoutSuccess -> {
                    backStack.clear()
                    backStack.add(LoginRoute)
                }
            }
        }
    }

    Content(
        sessionName = uiState.sessionName,
        sessionEmail = uiState.sessionEmail,
        isDarkTheme = uiState.isDarkTheme,
        onToggleTheme = vm::onToggleTheme,
        onClickManageCategory = { backStack.add(CategoryRoute) },
        onClickManageWallet = { backStack.add(WalletRoute) },
        onLogout = vm::onLogout,
    )
}

@Composable
private fun Content(
    sessionName: String,
    sessionEmail: String,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onClickManageCategory: () -> Unit,
    onClickManageWallet: () -> Unit,
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Setelan")
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Text(text = "Nama: $sessionName")
            Text(text = "Akun: $sessionEmail")

            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Rounded.DarkMode,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text("Tema gelap")
                },
                trailingContent = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleTheme,
                    )
                }
            )

            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Rounded.Wallet,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text("Dompet")
                },
                supportingContent = {
                    Text("Kelola dompet tabungan uang dan emas")
                },
                modifier = Modifier.clickable { onClickManageWallet() }
            )

            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Rounded.Category,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text("Kategori")
                },
                supportingContent = {
                    Text("Kelola kategori pengeluaran dan pemasukan transaksi")
                },
                modifier = Modifier.clickable { onClickManageCategory() }
            )

            ListItem(
                leadingContent = {
                    Icon(
                        Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text("Keluar")
                },
                modifier = Modifier.clickable { showLogoutConfirmation = true }
            )
        }

        if (showLogoutConfirmation)
            LogoutConfirmationDialog(
                onDismiss = { showLogoutConfirmation = false },
                onConfirm = {
                    showLogoutConfirmation = false
                    onLogout()
                },
            )
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Konfirmasi Keluar")
        },
        text = {
            when (isLoading) {
                true -> Box(modifier = Modifier.fillMaxWidth()) {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }

                else -> Text("Apakah kamu yakin akan keluar dari akun ini?")
            }
        },
        confirmButton = {
            if (!isLoading)
                TextButton(
                    onClick = onConfirm
                ) {
                    Text("Keluar")
                }
        },
        dismissButton = {
            if (!isLoading)
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Batal")
                }
        }
    )
}

@Preview(showBackground = true, name = "Setting")
@Composable
private fun HomeSettingPreview() {
    ArtaTheme {
        Content(
            sessionName = "Rizal",
            sessionEmail = "rizal@example.com",
            isDarkTheme = true,
            onToggleTheme = {},
            onLogout = {},
            onClickManageCategory = {},
            onClickManageWallet = {},
        )
    }
}

@Preview(showBackground = true, name = "Logout Confirmation")
@Composable
private fun LogoutConfirmationPreview() {
    ArtaTheme {
        LogoutConfirmationDialog(
            onDismiss = {},
            onConfirm = {},
        )
    }
}

@Preview(showBackground = true, name = "Logout Confirmation")
@Composable
private fun LogoutConfirmationLoadingPreview() {
    ArtaTheme {
        LogoutConfirmationDialog(
            isLoading = true,
            onDismiss = {},
            onConfirm = {},
        )
    }
}

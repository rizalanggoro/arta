package id.my.rizalanggoro.arta.feature.home.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun HomeSettingScreen(
    vm: HomeSettingVM = viewModel(factory = HomeSettingVM.Factory),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<HomeSettingEvent.LoggedOut>()
            .collect {
                backStack.clear()
                backStack.add(LoginRoute)
            }
    }

    Content(
        session = uiState.session,
        isDarkTheme = uiState.isDarkTheme,
        onToggleTheme = vm::onToggleTheme,
        onClickManageCategory = { backStack.add(CategoryRoute) },
        onClickManageWallet = { backStack.add(WalletRoute) },
        onClickLogout = vm::logout,
    )

    if (uiState.isLogoutOpen)
        LogoutDialog(
            onDismiss = { vm.onChangeLogoutDialog(isOpen = false) },
            onConfirm = { vm.logout() },
        )
}

@Composable
private fun Content(
    session: AuthSession? = null,
    isDarkTheme: Boolean = false,
    onToggleTheme: (Boolean) -> Unit = {},
    onClickLogout: () -> Unit = {},
    onClickManageCategory: () -> Unit = {},
    onClickManageWallet: () -> Unit = {},
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column {
                Text(
                    text = session?.name ?: "Tidak diketahui",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = session?.email ?: "Tidak diketahui",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

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
            modifier = Modifier.clickable { onClickLogout() }
        )
    }
}

@Composable
private fun LogoutDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    isLoading: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Keluar")
        },
        text = {
            when (isLoading) {
                true -> Box(modifier = Modifier.fillMaxWidth()) {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }

                else -> Text("Apakah Anda yakin akan keluar dari akun ini?")
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
            isDarkTheme = true,
        )
    }
}

@Preview(showBackground = true, name = "Logout Confirmation")
@Composable
private fun LogoutPreview() {
    ArtaTheme {
        LogoutDialog()
    }
}

@Preview(showBackground = true, name = "Logout Confirmation")
@Composable
private fun LogoutLoadingPreview() {
    ArtaTheme {
        LogoutDialog(
            isLoading = true,
        )
    }
}

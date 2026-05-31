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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategoryRoute
import id.my.rizalanggoro.arta.core.Routes.GoldTaxListRoute
import id.my.rizalanggoro.arta.core.Routes.LoginRoute
import id.my.rizalanggoro.arta.core.Routes.TestDialogRoute
import id.my.rizalanggoro.arta.core.Routes.UpdateRoute
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun HomeSettingScreen(
    vm: HomeSettingVM = hiltViewModel(),
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
        uiState = uiState,
        onToggleTheme = vm::onToggleTheme,
        onClickManageCategory = { backStack.add(CategoryRoute) },
        onClickManageWallet = { backStack.add(WalletRoute) },
        onClickManageGoldTax = { backStack.add(GoldTaxListRoute) },
        onClickUpdate = { backStack.add(UpdateRoute) },
        onClickLogout = vm::logout,
        onClickTest = {
            backStack.add(TestDialogRoute)
        }
    )

    if (uiState.isLogoutOpen)
        LogoutDialog(
            onDismiss = { vm.onChangeLogoutDialog(isOpen = false) },
            onConfirm = { vm.logout() },
        )
}

@Composable
private fun Content(
    uiState: HomeSettingUiState = HomeSettingUiState(),
    onToggleTheme: (Boolean) -> Unit = {},
    onClickManageWallet: () -> Unit = {},
    onClickManageCategory: () -> Unit = {},
    onClickManageGoldTax: () -> Unit = {},
    onClickUpdate: () -> Unit = {},
    onClickLogout: () -> Unit = {},
    onClickTest: () -> Unit = {},
) {
    LazyColumn {
        item {
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
                        .clickable {
                            onClickTest()
                        }
                ) {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = uiState.session?.name ?: "Tidak diketahui",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = uiState.session?.email ?: "Tidak diketahui",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            ListItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .clickable { onToggleTheme(!uiState.isDarkTheme) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                        checked = uiState.isDarkTheme,
                        onCheckedChange = onToggleTheme,
                    )
                }
            )
        }

        item {
            ListItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 2.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .clickable { onClickManageWallet() },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
            )
        }

        item {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 2.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .clickable { onClickManageCategory() },
            )
        }

        item {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                leadingContent = {
                    Icon(
                        Icons.Rounded.Balance,
                        null,
                    )
                },
                headlineContent = {
                    Text("Pajak emas")
                },
                supportingContent = {
                    Text("Atur preferensi pajak jual emas berdasarkan ukuran karat")
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 2.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .clickable { onClickManageGoldTax() }
            )
        }

        item {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                leadingContent = {
                    BadgedBox(
                        badge = {
                            if (uiState.hasUpdate)
                                Badge()
                        }
                    ) {
                        Icon(
                            Icons.Rounded.Update,
                            null,
                        )
                    }
                },
                headlineContent = {
                    Text("Pembaruan")
                },
                supportingContent = {
                    Text("Periksa dan unduh pembaruan aplikasi")
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 2.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .clickable { onClickUpdate() }
            )
        }

        item {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                leadingContent = {
                    Icon(
                        Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text("Keluar")
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 2.dp, bottom = 16.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .clickable { onClickLogout() }
            )
        }
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
            uiState = HomeSettingUiState(
                isDarkTheme = true
            )
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

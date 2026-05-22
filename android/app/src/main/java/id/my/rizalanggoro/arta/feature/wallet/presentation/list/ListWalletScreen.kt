package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.UpsertWalletRoute
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ListWalletScreen(vm: ListWalletVM = viewModel(factory = ListWalletVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        vm.loadWallets()
    }

    Content(
        wallets = uiState.wallets,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        deleteTarget = uiState.deleteTarget,
        selectedWallet = uiState.selectedWallet,
        onClickCreate = { backStack.add(UpsertWalletRoute()) },
        onClickEdit = { walletId -> backStack.add(UpsertWalletRoute(walletId = walletId)) },
        onSelectWallet = vm::onWalletSelected,
        onClickDelete = vm::onDeleteRequested,
        onDismissWalletActions = vm::dismissWalletActions,
        onDismissDelete = vm::dismissDeleteDialog,
        onConfirmDelete = vm::confirmDeleteWallet,
        onRetry = vm::loadWallets,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    wallets: List<Wallet> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    deleteTarget: Wallet? = null,
    selectedWallet: Wallet? = null,
    onClickCreate: () -> Unit = {},
    onClickEdit: (Int) -> Unit = {},
    onSelectWallet: (Wallet) -> Unit = {},
    onClickDelete: (Wallet) -> Unit = {},
    onDismissWalletActions: () -> Unit = {},
    onDismissDelete: () -> Unit = {},
    onConfirmDelete: (Wallet) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                title = { Text("Wallet") },
            )
        },
        floatingActionButton = {
            if (!isLoading) {
                FloatingActionButton(onClick = onClickCreate) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator()
                    }
                }

                errorMessage != null -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(text = errorMessage, style = MaterialTheme.typography.bodyLarge)
                            Button(onClick = onRetry) {
                                Text("Muat ulang")
                            }
                        }
                    }
                }

                wallets.isEmpty() -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = "Belum ada wallet.",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "Wallet akan muncul di sini setelah dibuat dari backend.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Button(onClick = onRetry) {
                                Text("Cek lagi")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn {
                        items(wallets, key = { it.id }) { wallet ->
                            ListItem(
                                headlineContent = {
                                    Text(wallet.name)
                                },
                                supportingContent = {
                                    Text(walletTypeLabel(wallet.type))
                                },
                                modifier = Modifier.clickable {
                                    onSelectWallet(wallet)
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    selectedWallet?.let { wallet ->
        WalletActionBottomSheet(
            wallet = wallet,
            onEdit = {
                onDismissWalletActions()
                onClickEdit(wallet.id)
            },
            onDelete = {
                onDismissWalletActions()
                onClickDelete(wallet)
            },
            onDismiss = onDismissWalletActions,
        )
    }

    if (deleteTarget != null) {
        DeleteWalletDialog(
            wallet = deleteTarget,
            onDismiss = onDismissDelete,
            onConfirmDelete = onConfirmDelete,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletActionBottomSheet(
    wallet: Wallet,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = wallet.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = walletTypeLabel(wallet.type),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ListItem(
                headlineContent = { Text("Edit") },
                leadingContent = {
                    Icon(Icons.Rounded.Edit, contentDescription = null)
                },
                modifier = Modifier.clickable(onClick = onEdit),
            )
            ListItem(
                headlineContent = { Text("Hapus") },
                leadingContent = {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                },
                modifier = Modifier.clickable(onClick = onDelete),
            )

            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    }
}

@Composable
private fun DeleteWalletDialog(
    wallet: Wallet,
    onDismiss: () -> Unit,
    onConfirmDelete: (Wallet) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus wallet?") },
        text = {
            Text(
                text = "Wallet \"${wallet.name}\" akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.",
            )
        },
        confirmButton = {
            Button(onClick = { onConfirmDelete(wallet) }) {
                Text("Hapus")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
    )
}

private fun walletTypeLabel(type: String): String {
    return when (type) {
        "cash_savings" -> "Tabungan Uang"
        "gold_savings" -> "Tabungan Emas"
        else -> type.replace('_', ' ')
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@Preview(showBackground = true, name = "Wallet List")
@Composable
private fun WalletListPreview() {
    ArtaTheme {
        Content(
            wallets = listOf(
                Wallet(
                    id = 1,
                    userId = 10,
                    name = "Utama",
                    type = "cash_savings",
                ),
                Wallet(
                    id = 2,
                    userId = 10,
                    name = "Emas",
                    type = "gold_savings",
                ),
            ),
        )
    }
}

@Preview(showBackground = true, name = "Wallet List - Loading")
@Composable
private fun WalletListLoadingPreview() {
    ArtaTheme { Content(isLoading = true) }
}

@Preview(showBackground = true, name = "Wallet List - Empty")
@Composable
private fun WalletListEmptyPreview() {
    ArtaTheme { Content() }
}

@Preview(showBackground = true, name = "Wallet List - Error")
@Composable
private fun WalletListErrorPreview() {
    ArtaTheme { Content(errorMessage = "Gagal memuat wallet") }
}

@Preview(showBackground = true, name = "Wallet Action Sheet")
@Composable
private fun WalletActionBottomSheetPreview() {
    ArtaTheme {
        WalletActionBottomSheet(
            wallet = Wallet(
                id = 1,
                userId = 10,
                name = "Utama",
                type = "cash_savings",
            ),
            onEdit = {},
            onDelete = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "Delete Wallet Dialog")
@Composable
private fun DeleteWalletDialogPreview() {
    ArtaTheme {
        DeleteWalletDialog(
            wallet = Wallet(
                id = 2,
                userId = 10,
                name = "Emas",
                type = "gold_savings",
            ),
            onDismiss = {},
            onConfirmDelete = {},
        )
    }
}

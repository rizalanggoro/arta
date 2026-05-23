package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.UpsertWalletRoute
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.feature.wallet.presentation.list.component.WalletActionBS
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ListWalletScreen(vm: ListWalletVM = viewModel(factory = ListWalletVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickCreate = { backStack.add(UpsertWalletRoute()) },
        onSelectWallet = vm::onWalletSelected,
        onClickRetry = vm::loadWallets,
        onClickBack = { backStack.removeLastOrNull() },
    )

    if (uiState.selectedWallet != null)
        WalletActionBS(
            wallet = uiState.selectedWallet!!,
            onClickEdit = { backStack.add(UpsertWalletRoute(walletId = uiState.selectedWallet!!.id!!)) },
            onClickDelete = vm::onActionDeleteClicked,
            onDismissRequest = vm::onActionDismissed,
        )

    if (uiState.deleteTarget != null)
        ConfirmDialog(
            title = "Hapus",
            description = "Apakah Anda yakin akan menghapus dompet " +
                    "\"${uiState.deleteTarget!!.name}\"? Tindakan ini tidak dapat dipulihkan",
            onDismissRequest = vm::onDialogDismissed,
            onConfirmRequest = vm::confirmDeleteWallet,
            isLoading = uiState.isDeleting,
            confirmText = "Hapus"
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: ListWalletUiState = ListWalletUiState(),
    onClickCreate: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onSelectWallet: (DomainWallet) -> Unit = {},
    onClickRetry: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            null,
                        )
                    }
                },
                title = { Text("Kelola Dompet") },
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading) {
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
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingIndicator()
                    }
                }

                uiState.errorMessage != null -> ErrorPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    message = uiState.errorMessage,
                    onClickRetry = onClickRetry
                )

                uiState.wallets.isEmpty() -> EmptyPlaceholder(
                    modifier = Modifier.fillMaxSize()
                )

                else -> {
                    LazyColumn {
                        items(uiState.wallets) { wallet ->
                            ListItem(
                                headlineContent = {
                                    Text(wallet.data.name.orEmpty())
                                },
                                supportingContent = {
                                    Text(wallet.data.type.orEmpty().toWalletName())
                                },
                                modifier = Modifier.clickable {
                                    onSelectWallet(wallet.data)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Wallet List")
@Composable
private fun WalletListPreview() {
    ArtaTheme {
        Content(
            uiState = ListWalletUiState(
                wallets = listOf(
                    DtoWallet(
                        data = DomainWallet(
                            id = 1,
                            userID = 10,
                            name = "Utama",
                            type = "cash_savings",
                        ),
                    ),
                    DtoWallet(
                        data = DomainWallet(
                            id = 2,
                            userID = 10,
                            name = "Emas",
                            type = "gold_savings",
                        ),
                    ),
                ),
            )
        )
    }
}

@Preview(showBackground = true, name = "Wallet List - Loading")
@Composable
private fun WalletListLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = ListWalletUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, name = "Wallet List - Empty")
@Composable
private fun WalletListEmptyPreview() {
    ArtaTheme { Content() }
}

@Preview(showBackground = true, name = "Wallet List - Error")
@Composable
private fun WalletListErrorPreview() {
    ArtaTheme {
        Content(
            uiState = ListWalletUiState(
                errorMessage = "Gagal memuat wallet"
            )
        )
    }
}

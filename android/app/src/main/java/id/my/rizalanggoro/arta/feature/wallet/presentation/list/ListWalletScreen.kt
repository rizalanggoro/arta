package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.WalletListItem
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ListWalletScreen(vm: ListWalletVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickCreate = { backStack.add(WalletRoute.Upsert()) },
        onLongClickWallet = {
            backStack.add(
                WalletRoute.ActionSheet(
                    walletId = it.id
                )
            )
        },
        onClickRetry = vm::loadWallets,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: ListWalletUiState = ListWalletUiState(),
    onClickCreate: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onLongClickWallet: (DomainWallet) -> Unit = {},
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

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    itemsIndexed(uiState.wallets) { index, wallet ->
                        WalletListItem(
                            wallet = wallet.data,
                            index = index,
                            size = uiState.wallets.size,
                            onLongClick = onLongClickWallet,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    item {
                        Text(
                            "Tekan dan tahan untuk melihat opsi lainnya",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ArtaTheme {
        Content(
            uiState = ListWalletUiState(
                wallets = listOf(
                    DtoWallet(
                        data = DomainWallet(
                            id = 1,
                            userId = 10,
                            name = "Utama",
                            type = "cash_savings",
                            createdAt = "2026-05-23T00:00:00Z",
                            updatedAt = "2026-05-23T00:00:00Z",
                        ),
                    ),
                    DtoWallet(
                        data = DomainWallet(
                            id = 2,
                            userId = 10,
                            name = "Emas",
                            type = "gold_savings",
                            createdAt = "2026-05-23T00:00:00Z",
                            updatedAt = "2026-05-23T00:00:00Z",
                        ),
                    ),
                ),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
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
private fun EmptyPreview() {
    ArtaTheme { Content() }
}

@Preview(showBackground = true, name = "Wallet List - Error")
@Composable
private fun ErrorPreview() {
    ArtaTheme {
        Content(
            uiState = ListWalletUiState(
                errorMessage = "Gagal memuat wallet"
            )
        )
    }
}

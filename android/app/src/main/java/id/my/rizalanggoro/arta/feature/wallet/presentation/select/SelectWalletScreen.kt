package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SelectWalletScreen(
    vm: SelectWalletVM = viewModel(factory = SelectWalletVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.WalletSelected>()
            .collect { backStack.removeLastOrNull() }

        AppEventBus.event
            .filterIsInstance<AppEvent.WalletChanged>()
            .collect { vm.loadWallets() }
    }

    Content(
        selectedWallet = uiState.selectedWallet,
        wallets = uiState.wallets,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onClickBack = { backStack.removeLastOrNull() },
        onReload = vm::loadWallets,
        onClickWallet = vm::selectWallet,
        onClickAdd = { backStack.add(Routes.WalletCreateRoute) }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    selectedWallet: Wallet? = null,
    wallets: List<Wallet> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onClickBack: () -> Unit = {},
    onReload: () -> Unit = {},
    onClickWallet: (Wallet) -> Unit = {},
    onClickAdd: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Dompet") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (!isLoading)
                FloatingActionButton(onClick = onClickAdd) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null
                    )
                }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                            Text(errorMessage)
                            Button(onClick = onReload) { Text("Muat ulang") }
                        }
                    }
                }

                wallets.isEmpty() -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text("Belum ada wallet.")
                            Text(
                                text = "Buat wallet dulu agar bisa dipakai di transaksi.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(wallets) { wallet ->
                            ListItem(
                                leadingContent = {
                                    RadioButton(
                                        selected = selectedWallet?.id == wallet.id,
                                        onClick = { onClickWallet(wallet) }
                                    )
                                },
                                headlineContent = {
                                    Text(wallet.name)
                                },
                                supportingContent = {
                                    Text(walletTypeLabel(wallet.type))
                                },
                                modifier = Modifier.clickable {
                                    onClickWallet(wallet)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(88.dp)) }
                    }
                }
            }
        }
    }
}

private fun walletTypeLabel(type: String): String {
    return when (type) {
        "cash_savings" -> "Tabungan Uang"
        "gold_savings" -> "Tabungan Emas"
        else -> type.replace('_', ' ')
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@Preview(showBackground = true, name = "Wallet Selector")
@Composable
private fun SelectWalletPreview() {
    ArtaTheme {
        Content(
            wallets = listOf(
                Wallet(id = 1, userId = 1, name = "Dompet Harian", type = "cash_savings"),
            ),
        )
    }
}

@Preview(showBackground = true, name = "Wallet Selector - Loading")
@Composable
private fun SelectWalletLoadingPreview() {
    ArtaTheme {
        Content(isLoading = true)
    }
}

@Preview(showBackground = true, name = "Wallet Selector - Error")
@Composable
private fun SelectWalletErrorPreview() {
    ArtaTheme {
        Content(errorMessage = "Gagal memuat data wallet")
    }
}

@Preview(showBackground = true, name = "Wallet Selector - Empty")
@Composable
private fun SelectWalletEmptyPreview() {
    ArtaTheme {
        Content(wallets = emptyList())
    }
}

@Preview(showBackground = true, name = "Wallet Selector - With Items")
@Composable
private fun SelectWalletItemsPreview() {
    ArtaTheme {
        Content(
            wallets = listOf(
                Wallet(id = 1, userId = 1, name = "Dompet Harian", type = "cash_savings"),
                Wallet(id = 2, userId = 1, name = "Tabungan Emas", type = "gold_savings"),
            ),
        )
    }
}
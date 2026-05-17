package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SelectWalletScreen(
    vm: SelectWalletVM = viewModel(factory = SelectWalletVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        vm.loadWallets()
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                SelectWalletEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Wallet") },
                navigationIcon = {
                    TextButton(onClick = { backStack.removeLastOrNull() }) { Text("Batal") }
                },
            )
        },
    ) { paddingValues ->
        Content(
            wallets = uiState.wallets,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onReload = vm::loadWallets,
            onSelect = vm::selectWallet,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
        )
    }
}

@Composable
private fun Content(
    wallets: List<Wallet> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onReload: () -> Unit = {},
    onSelect: (Wallet) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Pilih wallet yang akan dipakai di transaksi.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(errorMessage ?: "Gagal memuat wallet")
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(wallets, key = { it.id }) { wallet ->
                        SelectWalletCard(
                            wallet = wallet,
                            onClick = { onSelect(wallet) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectWalletCard(
    wallet: Wallet,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(wallet.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = walletTypeLabel(wallet.type),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "ID wallet: ${wallet.id}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun walletTypeLabel(type: String): String {
    return when (type) {
        "cash_savings" -> "Tabungan Uang"
        "gold_savings" -> "Tabungan Emas"
        else -> type.replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@Preview(showBackground = true, name = "Wallet Selector")
@Composable
private fun SelectWalletPreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, name = "Wallet Selector - Loading")
@Composable
private fun SelectWalletLoadingPreview() {
    ArtaTheme { Content() }
}

@Preview(showBackground = true, name = "Wallet Selector - Empty")
@Composable
private fun SelectWalletEmptyPreview() {
    ArtaTheme { Content() }
}

@Preview(showBackground = true, name = "Wallet Selector - With Items")
@Composable
private fun SelectWalletItemsPreview() {
    ArtaTheme {
        Content()
    }
}
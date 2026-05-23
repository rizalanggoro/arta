package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SelectWalletScreen(
    vm: SelectWalletVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.WalletSelected>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        uiState = uiState,
        onClickRetry = vm::loadWallets,
        onClickWallet = vm::onWalletSelected,
        onClickManageWallet = {
            backStack.removeLastOrNull()
            backStack.add(Routes.WalletRoute)
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    uiState: SelectWalletUiState = SelectWalletUiState(),
    onClickRetry: () -> Unit = {},
    onClickWallet: (DomainWallet) -> Unit = {},
    onClickManageWallet: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Pilih Dompet",
                style = MaterialTheme.typography.titleLarge,
            )
            FilledTonalIconButton(onClick = onClickManageWallet) {
                Icon(
                    Icons.Rounded.Settings,
                    null
                )
            }
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }
            }

            uiState.errorMessage != null -> ErrorPlaceholder(
                modifier = Modifier.padding(16.dp),
                message = uiState.errorMessage,
                onClickRetry = onClickRetry
            )

            uiState.wallets.isEmpty() -> EmptyPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(uiState.wallets) { wallet ->
                        ListItem(
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent
                            ),
                            leadingContent = {
                                RadioButton(
                                    selected = uiState.selectedWallet?.id == wallet.data.id,
                                    onClick = { onClickWallet(wallet.data) },
                                )
                            },
                            headlineContent = {
                                Text(wallet.data.name.orEmpty())
                            },
                            supportingContent = {
                                Text(wallet.data.type.orEmpty().toWalletName())
                            },
                            modifier = Modifier.clickable {
                                onClickWallet(wallet.data)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Wallet Selector")
@Composable
private fun SelectWalletPreview() {
    ArtaTheme {
        Content(
            uiState = SelectWalletUiState(
                wallets = listOf(
                    DtoWallet(
                        data = DomainWallet(
                            id = 1,
                            userId = 1,
                            name = "Dompet Harian",
                            type = "cash_savings"
                            ,createdAt = "2026-05-23T00:00:00Z",
                            updatedAt = "2026-05-23T00:00:00Z",
                        )
                    ),
                    DtoWallet(
                        data = DomainWallet(
                            id = 2,
                            userId = 1,
                            name = "Dompet Harian",
                            type = "cash_savings"
                            ,createdAt = "2026-05-23T00:00:00Z",
                            updatedAt = "2026-05-23T00:00:00Z",
                        )
                    ),
                    DtoWallet(
                        data = DomainWallet(
                            id = 3,
                            userId = 1,
                            name = "Dompet Harian",
                            type = "cash_savings"
                            ,createdAt = "2026-05-23T00:00:00Z",
                            updatedAt = "2026-05-23T00:00:00Z",
                        )
                    ),
                )
            ),
        )
    }
}

@Preview(showBackground = true, name = "Wallet Selector - Loading")
@Composable
private fun SelectWalletLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = SelectWalletUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, name = "Wallet Selector - Error")
@Composable
private fun SelectWalletErrorPreview() {
    ArtaTheme {
        Content(
            uiState = SelectWalletUiState(
                errorMessage = "Gagal memuat data wallet"
            )
        )
    }
}

@Preview(showBackground = true, name = "Wallet Selector - Empty")
@Composable
private fun SelectWalletEmptyPreview() {
    ArtaTheme {
        Content(
            uiState = SelectWalletUiState(
                wallets = emptyList()
            )
        )
    }
}

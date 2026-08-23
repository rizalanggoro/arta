package id.my.rizalanggoro.arta.feature.wallet.presentation.select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.LocalBottomSheetEndAction
import id.my.rizalanggoro.arta.shared.component.LocalBottomSheetTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SelectWalletScreen(
    vm: SelectWalletVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val bottomSheetTitle = LocalBottomSheetTitle.current
    val bottomSheetEndAction = LocalBottomSheetEndAction.current

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.WalletSelected>()
            .collect {
                delay(300)
                backStack.removeLastOrNull()
            }
    }

    LaunchedEffect(bottomSheetTitle, bottomSheetEndAction) {
        bottomSheetTitle?.value = "Pilih Dompet"
        bottomSheetEndAction?.value = {
            IconButton(
                onClick = {
                    backStack.removeLastOrNull()
                    backStack.add(WalletRoute.List)
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    MiuixIcons.Settings,
                    null
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bottomSheetTitle?.value = null
            bottomSheetEndAction?.value = null
        }
    }

    Content(
        uiState = uiState,
        onClickRetry = vm::loadWallets,
        onClickWallet = vm::onWalletSelected,
        onClickCancel = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun Content(
    uiState: SelectWalletUiState = SelectWalletUiState(),
    onClickRetry: () -> Unit = {},
    onClickWallet: (DomainWallet) -> Unit = {},
    onClickCancel: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
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
                        RadioButtonPreference(
                            title = wallet.data.name.orEmpty(),
                            summary = wallet.data.type.orEmpty().toWalletName(),
                            selected = uiState.selectedWallet?.id == wallet.data.id,
                            onClick = { onClickWallet(wallet.data) },
                        )
                    }
                }
                Button(
                    onClick = onClickCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Batal")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Wallet Selector")
@Composable
private fun SelectWalletPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = SelectWalletUiState(
                wallets = listOf(
                    DtoWallet(
                        data = DomainWallet(
                            id = 1,
                            userId = 1,
                            name = "Dompet Harian",
                            type = "cash_savings",
                            createdAt = "2026-05-23T00:00:00Z",
                            updatedAt = "2026-05-23T00:00:00Z",
                        )
                    ),
                    DtoWallet(
                        data = DomainWallet(
                            id = 2,
                            userId = 1,
                            name = "Dompet Harian",
                            type = "cash_savings",
                            createdAt = "2026-05-23T00:00:00Z",
                            updatedAt = "2026-05-23T00:00:00Z",
                        )
                    ),
                    DtoWallet(
                        data = DomainWallet(
                            id = 3,
                            userId = 1,
                            name = "Dompet Harian",
                            type = "cash_savings",
                            createdAt = "2026-05-23T00:00:00Z",
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
        Content(
            uiState = SelectWalletUiState(
                wallets = emptyList()
            )
        )
    }
}

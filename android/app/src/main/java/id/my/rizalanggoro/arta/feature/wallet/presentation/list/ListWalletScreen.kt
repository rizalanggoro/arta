package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoWallet
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.WalletListItem
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun ListWalletScreen(vm: ListWalletVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickCreate = { backStack.add(WalletRoute.Upsert()) },
        onClickEdit = { backStack.add(WalletRoute.Upsert(walletId = it.id)) },
        onClickDelete = { backStack.add(WalletRoute.Delete(walletId = it.id)) },
        onClickRetry = vm::loadWallets,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: ListWalletUiState = ListWalletUiState(),
    onClickCreate: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickEdit: (DomainWallet) -> Unit = {},
    onClickDelete: (DomainWallet) -> Unit = {},
    onClickRetry: () -> Unit = {},
) {
    var actionWallet by remember { mutableStateOf<DomainWallet?>(null) }
    ArtaMiuixTheme {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = "Kelola Dompet",
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                MiuixIcons.Back,
                                null,
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                if (!uiState.isLoading) {
                    FloatingActionButton(onClick = onClickCreate) {
                        Icon(
                            MiuixIcons.Add,
                            contentDescription = null,
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
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

                    else -> LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 12.dp,
                            bottom = 96.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            Card {
                                uiState.wallets.forEach { wallet ->
                                    Box {
                                        WalletListItem(
                                            wallet = wallet.data,
                                            onClick = { actionWallet = it },
                                        )

                                        if (actionWallet?.id == wallet.data.id) {
                                            WindowListPopup(
                                                show = true,
                                                onDismissRequest = { actionWallet = null },
                                                alignment = PopupPositionProvider.Align.End,
                                            ) {
                                                ListPopupColumn {
                                                    listOf(
                                                        DropdownItem(
                                                            text = "Ubah",
                                                            icon = { iconModifier ->
                                                                Icon(MiuixIcons.Edit, null, modifier = iconModifier)
                                                            },
                                                        ),
                                                        DropdownItem(
                                                            text = "Hapus",
                                                            icon = { iconModifier ->
                                                                Icon(
                                                                    MiuixIcons.Delete,
                                                                    null,
                                                                    modifier = iconModifier,
                                                                    tint = MiuixTheme.colorScheme.error
                                                                )
                                                            },
                                                        ),
                                                    ).forEachIndexed { index, item ->
                                                        DropdownImpl(
                                                            item = item,
                                                            optionSize = 2,
                                                            isSelected = false,
                                                            index = index,
                                                            onSelectedIndexChange = {
                                                                actionWallet = null
                                                                when (index) {
                                                                    0 -> onClickEdit(wallet.data)
                                                                    else -> onClickDelete(wallet.data)
                                                                }
                                                            },
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Ketuk dompet untuk melihat opsi lainnya",
                                fontSize = 13.sp,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
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

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    Content(
        uiState = ListWalletUiState(
            isLoading = true
        )
    )
}

@Preview(showBackground = true, name = "Wallet List - Empty")
@Composable
private fun EmptyPreview() {
    Content()
}

@Preview(showBackground = true, name = "Wallet List - Error")
@Composable
private fun ErrorPreview() {
    Content(
        uiState = ListWalletUiState(
            errorMessage = "Gagal memuat wallet"
        )
    )
}

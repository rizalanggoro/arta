package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.GoldListItem
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.PullToRefreshState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun HomeGoldScreen(
    vm: HomeGoldVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.GoldChanged>()
            .collect { vm.loadGolds() }
    }

    Content(
        uiState = uiState,
        onRefresh = vm::loadGolds,
        onClickRetry = vm::loadGolds,
        onClickItem = { backStack.add(GoldRoute.Detail(id = it.id)) },
        onClickEdit = { backStack.add(GoldRoute.Upsert(goldId = it.id)) },
        onClickDelete = { backStack.add(GoldRoute.Delete(goldId = it.id)) },
    )
}

@Composable
private fun Content(
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    uiState: HomeGoldUiState = HomeGoldUiState(),
    onRefresh: () -> Unit = {},
    onClickRetry: () -> Unit = {},
    onClickItem: (DomainGold) -> Unit = {},
    onClickEdit: (DomainGold) -> Unit = {},
    onClickDelete: (DomainGold) -> Unit = {},
) {
    var actionGold by remember { mutableStateOf<DomainGold?>(null) }
    ArtaMiuixTheme {
        when {
            uiState.isLoading && uiState.golds.isEmpty() -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
            }

            uiState.errorMessage != null && uiState.golds.isEmpty() -> ErrorPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                onClickRetry = onClickRetry
            )

            uiState.golds.isEmpty() -> EmptyPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )

            else -> PullToRefresh(
                isRefreshing = uiState.isLoading && uiState.golds.isNotEmpty(),
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
                pullToRefreshState = pullToRefreshState,
            ) {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            uiState.golds.forEach { gold ->
                                Box {
                                    GoldListItem(
                                        gold = gold,
                                        onClick = { onClickItem(it) },
                                        onLongClick = { actionGold = it },
                                    )

                                    if (actionGold?.id == gold.data.id) {
                                        WindowListPopup(
                                            show = true,
                                            onDismissRequest = { actionGold = null },
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
                                                            actionGold = null
                                                            when (index) {
                                                                0 -> onClickEdit(gold.data)
                                                                else -> onClickDelete(gold.data)
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
                            text = "Ketuk untuk membuka detail • Tekan dan tahan untuk opsi lainnya",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }

                    item {
                        Box(modifier = Modifier.height((56 + 32).dp))
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
        uiState = HomeGoldUiState(
            golds = List(5) {
                DtoGold(
                    data = DomainGold(
                        carat = 24.0,
                        createdAt = "2026-05-25T14:38:00.000+07:00",
                        date = "2026-05-25T14:38:00.000+07:00",
                        grams = 3.3,
                        id = 1,
                        notes = "",
                        price = 1500000.0,
                        type = "jewelry",
                        updatedAt = "2026-05-25T14:38:00.000+07:00",
                        walletId = 1
                    ),
                    profit = ((it - 1) * 500000.0),
                    sellPrice = (1500000 + ((it - 1) * 500000.0)),
                )
            },
            isLoading = false,
            errorMessage = null,
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    Content(
        uiState = HomeGoldUiState(
            isLoading = true
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    Content(
        uiState = HomeGoldUiState(
            golds = emptyList()
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    Content(
        uiState = HomeGoldUiState(
            errorMessage = "Terjadi kesalahan tak terduga"
        )
    )
}

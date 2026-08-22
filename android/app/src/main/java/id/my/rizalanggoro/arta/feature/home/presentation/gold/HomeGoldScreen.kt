package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.openapi.models.DtoGold
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.GoldListItem
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.PullToRefreshState
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

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
        onLongClickGold = {
            backStack.add(
                GoldRoute.ActionSheet(
                    goldId = it.id
                )
            )
        }
    )
}

@Composable
private fun Content(
    pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    uiState: HomeGoldUiState = HomeGoldUiState(),
    onRefresh: () -> Unit = {},
    onClickRetry: () -> Unit = {},
    onLongClickGold: (DomainGold) -> Unit = {},
) {
    MiuixTheme(
        colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
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
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(uiState.golds) { index, gold ->
                        GoldListItem(
                            gold = gold,
                            onLongClick = onLongClickGold,
                            index = index,
                            size = uiState.golds.size
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

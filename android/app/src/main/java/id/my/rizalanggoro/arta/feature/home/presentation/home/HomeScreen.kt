package id.my.rizalanggoro.arta.feature.home.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.application.route.HomeRoute
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.application.route.WalletRoute
import id.my.rizalanggoro.arta.core.constant.TransactionGroupType
import id.my.rizalanggoro.arta.core.constant.TransactionTimeRangeType
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.HomeGoldDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldScreen
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionScreen
import id.my.rizalanggoro.arta.feature.transaction.presentation.action.TransactionFilterVM
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import top.yukonga.miuix.kmp.basic.Badge
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.BankCards
import top.yukonga.miuix.kmp.icon.extended.Filter
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Layers
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.menu.WindowIconCascadingDropdownMenu
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeScreen(
    vm: HomeVM = hiltViewModel(),
    filterVm: TransactionFilterVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()
    val filterUiState by filterVm.uiState.collectAsState()
    val destinations = walletDestinations(uiState.selectedWallet?.type)

    val walletType = uiState.selectedWallet?.type

    val homeBackStack = when (walletType) {
        "cash_savings" -> rememberNavBackStack(HomeRoute.CashDashboard)
        "gold_savings" -> rememberNavBackStack(HomeRoute.GoldDashboard)
        else -> null
    }

    Content(
        destinations = destinations,
        uiState = uiState,
        onClickSelectWallet = { backStack.add(WalletRoute.Select) },
        homeBackStack = homeBackStack,
        onClickFab = {
            when (walletType) {
                "cash_savings" -> backStack.add(TransactionRoute.Upsert())
                "gold_savings" -> backStack.add(GoldRoute.Upsert())
            }
        },
        filterGroupBy = filterUiState.groupBy,
        filterTimeRange = filterUiState.timeRange,
        onSelectGroupBy = filterVm::onGroupByChanged,
        onSelectTimeRange = filterVm::onTimeRangeChanged,
        entryProvider = entryProvider {
            entry<HomeRoute.CashDashboard> { HomeCashDashboardScreen() }
            entry<HomeRoute.GoldDashboard> { HomeGoldDashboardScreen() }
            entry<HomeRoute.ListTransaction> { HomeTransactionScreen() }
            entry<HomeRoute.ListGold> { HomeGoldScreen() }
            entry<HomeRoute.Setting> { HomeSettingScreen() }
        },
        hasUpdate = AppEventBus.updateEvent.collectAsState().value
    )
}

@Composable
private fun Content(
    destinations: List<HomeDestination>,
    uiState: HomeUiState = HomeUiState(),
    onClickSelectWallet: () -> Unit,
    homeBackStack: (NavBackStack<NavKey>)? = null,
    entryProvider: ((NavKey) -> NavEntry<NavKey>)? = null,
    onClickFab: () -> Unit = {},
    hasUpdate: Boolean = false,
    filterGroupBy: TransactionGroupType = TransactionGroupType.CATEGORY,
    filterTimeRange: TransactionTimeRangeType = TransactionTimeRangeType.DAILY,
    onSelectGroupBy: (TransactionGroupType) -> Unit = {},
    onSelectTimeRange: (TransactionTimeRangeType) -> Unit = {},
) {
    val lastDestination = homeBackStack?.lastOrNull()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = uiState.selectedWallet?.name.let {
                    if (it != null && lastDestination != HomeRoute.Setting) it
                    else "Arta"
                },
                actions = {
                    if (lastDestination == HomeRoute.ListTransaction)
                        FilterMenuButton(
                            groupBy = filterGroupBy,
                            timeRange = filterTimeRange,
                            onSelectGroupBy = onSelectGroupBy,
                            onSelectTimeRange = onSelectTimeRange,
                        )

                    if (lastDestination != HomeRoute.Setting)
                        IconButton(onClick = onClickSelectWallet) {
                            Icon(
                                MiuixIcons.BankCards,
                                null
                            )
                        }
                },
            )
        },
        bottomBar = {
            if (destinations.isNotEmpty() && homeBackStack != null)
                NavigationBar {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = homeBackStack.lastOrNull() == destination.route,
                            onClick = {
                                homeBackStack.removeFirstOrNull()
                                homeBackStack.add(destination.route)
                            },
                            icon = destination.icon,
                            label = destination.label,
                            badge = if (destination.route == HomeRoute.Setting && hasUpdate) ({
                                Badge()
                            }) else null
                        )
                    }
                }
        },
        floatingActionButton = {
            if (lastDestination != HomeRoute.Setting) {
                FloatingActionButton(onClick = onClickFab) {
                    Icon(
                        MiuixIcons.Add,
                        contentDescription = null
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
            if (homeBackStack != null && entryProvider != null)
                NavDisplay(
                    backStack = homeBackStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider
                )
        }
    }
}

@Composable
private fun FilterMenuButton(
    groupBy: TransactionGroupType,
    timeRange: TransactionTimeRangeType,
    onSelectGroupBy: (TransactionGroupType) -> Unit,
    onSelectTimeRange: (TransactionTimeRangeType) -> Unit,
) {
    WindowIconCascadingDropdownMenu(
        entries = listOf(
            DropdownEntry(
                items = listOf(
                    DropdownItem(
                        text = "Kategori",
                        enabled = true,
                        selected = groupBy == TransactionGroupType.CATEGORY,
                        onClick = { onSelectGroupBy(TransactionGroupType.CATEGORY) },
                    ),
                    DropdownItem(
                        text = "Transaksi",
                        enabled = true,
                        selected = groupBy == TransactionGroupType.TRANSACTION,
                        onClick = { onSelectGroupBy(TransactionGroupType.TRANSACTION) },
                    ),
                )
            ),
            DropdownEntry(
                items = listOf(
                    DropdownItem(
                        text = "Rentang waktu",
                        children = listOf(
                            DropdownItem(
                                text = "Harian",
                                enabled = true,
                                selected = timeRange == TransactionTimeRangeType.DAILY,
                                onClick = { onSelectTimeRange(TransactionTimeRangeType.DAILY) },
                            ),
                            DropdownItem(
                                text = "Mingguan",
                                enabled = true,
                                selected = timeRange == TransactionTimeRangeType.WEEKLY,
                                onClick = { onSelectTimeRange(TransactionTimeRangeType.WEEKLY) },
                            ),
                            DropdownItem(
                                text = "Bulanan",
                                enabled = true,
                                selected = timeRange == TransactionTimeRangeType.MONTHLY,
                                onClick = { onSelectTimeRange(TransactionTimeRangeType.MONTHLY) },
                            ),
                        ),
                    ),
                )
            ),
        ),
    ) {
        Icon(
            MiuixIcons.Filter,
            null
        )
    }
}

private data class HomeDestination(
    val label: String,
    val icon: ImageVector,
    val route: NavKey,
)

private fun walletDestinations(type: String?): List<HomeDestination> {
    if (type == null) return emptyList()
    val isCash = type == "cash_savings"
    return listOf(
        HomeDestination(
            label = "Ringkasan",
            icon = MiuixIcons.GridView,
            route = when (isCash) {
                true -> HomeRoute.CashDashboard
                else -> HomeRoute.GoldDashboard
            }
        ),
        when (isCash) {
            true -> HomeDestination(
                label = "Transaksi",
                icon = MiuixIcons.BankCards,
                route = HomeRoute.ListTransaction
            )

            else -> HomeDestination(
                label = "Emas",
                icon = MiuixIcons.Layers,
                route = HomeRoute.ListGold
            )
        },
        HomeDestination(
            label = "Pengaturan",
            icon = MiuixIcons.Settings,
            route = HomeRoute.Setting
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeCashPreview() {
    ArtaMiuixTheme {
        Content(
            destinations = walletDestinations("cash_savings"),
            onClickSelectWallet = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeGoldPreview() {
    ArtaMiuixTheme {
        Content(
            destinations = walletDestinations("gold_savings"),
            onClickSelectWallet = {},
        )
    }
}

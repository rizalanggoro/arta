package id.my.rizalanggoro.arta.feature.home.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.application.Routes.HomeCashDashboardRoute
import id.my.rizalanggoro.arta.core.application.Routes.HomeGoldDashboardRoute
import id.my.rizalanggoro.arta.core.application.Routes.HomeGoldRoute
import id.my.rizalanggoro.arta.core.application.Routes.HomeSettingRoute
import id.my.rizalanggoro.arta.core.application.Routes.HomeTransactionRoute
import id.my.rizalanggoro.arta.core.application.Routes.TransactionUpsertRoute
import id.my.rizalanggoro.arta.core.application.Routes.UpsertGoldRoute
import id.my.rizalanggoro.arta.core.application.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.HomeGoldDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldScreen
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionScreen
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeScreen(
    vm: HomeVM = hiltViewModel(),
) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()
    val destinations = walletDestinations(uiState.selectedWallet?.type)

    val walletType = uiState.selectedWallet?.type

    val homeBackStack = when (walletType) {
        "cash_savings" -> rememberNavBackStack(HomeCashDashboardRoute)
        "gold_savings" -> rememberNavBackStack(HomeGoldDashboardRoute)
        else -> null
    }

    Content(
        destinations = destinations,
        uiState = uiState,
        onClickSelectWallet = { backStack.add(WalletSelectRoute) },
        homeBackStack = homeBackStack,
        onClickFab = {
            when (walletType) {
                "cash_savings" -> backStack.add(TransactionUpsertRoute())
                "gold_savings" -> backStack.add(UpsertGoldRoute())
            }
        },
        entryProvider = entryProvider {
            entry<HomeCashDashboardRoute> { HomeCashDashboardScreen() }
            entry<HomeGoldDashboardRoute> { HomeGoldDashboardScreen() }
            entry<HomeTransactionRoute> { HomeTransactionScreen() }
            entry<HomeGoldRoute> { HomeGoldScreen() }
            entry<HomeSettingRoute> { HomeSettingScreen() }
        },
        hasUpdate = AppEventBus.updateEvent.collectAsState().value
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    destinations: List<HomeDestination>,
    uiState: HomeUiState = HomeUiState(),
    onClickSelectWallet: () -> Unit,
    homeBackStack: (NavBackStack<NavKey>)? = null,
    entryProvider: ((NavKey) -> NavEntry<NavKey>)? = null,
    onClickFab: () -> Unit = {},
    hasUpdate: Boolean = false,
) {
    val lastDestination = homeBackStack?.lastOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = when {
                        lastDestination in listOf(
                            HomeCashDashboardRoute,
                            HomeGoldDashboardRoute,
                        ) -> MaterialTheme.colorScheme.surfaceContainer

                        else -> Color.Unspecified
                    }
                ),
                title = {
                    Text(
                        text = uiState.selectedWallet?.name.let {
                            if (it != null && lastDestination != HomeSettingRoute) it
                            else "Arta"
                        }
                    )
                },
                actions = {
                    if (lastDestination != HomeSettingRoute)
                        IconButton(onClick = onClickSelectWallet) {
                            Icon(
                                Icons.Rounded.Wallet,
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
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (destination.route == HomeSettingRoute && hasUpdate)
                                            Badge()
                                    }
                                ) {
                                    Icon(
                                        destination.icon,
                                        contentDescription = null
                                    )
                                }
                            },
                            label = { Text(destination.label) },
                        )
                    }
                }
        },
        floatingActionButton = {
            if (lastDestination != HomeSettingRoute) {
                FloatingActionButton(onClick = onClickFab) {
                    Icon(
                        Icons.Rounded.Add,
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
            icon = Icons.Rounded.Dashboard,
            route = when (isCash) {
                true -> HomeCashDashboardRoute
                else -> HomeGoldDashboardRoute
            }
        ),
        when (isCash) {
            true -> HomeDestination(
                label = "Transaksi",
                icon = Icons.Rounded.Payment,
                route = HomeTransactionRoute
            )

            else -> HomeDestination(
                label = "Emas",
                icon = Icons.Rounded.Balance,
                route = HomeGoldRoute
            )
        },
        HomeDestination(
            label = "Pengaturan",
            icon = Icons.Rounded.Settings,
            route = HomeSettingRoute
        ),
    )
}

@Preview(showBackground = true, name = "Cash Wallet Home")
@Composable
private fun HomeCashPreview() {
    ArtaTheme {
        Content(
            destinations = walletDestinations("cash_savings"),
            onClickSelectWallet = {},
        )
    }
}

@Preview(showBackground = true, name = "Gold Wallet Home")
@Composable
private fun HomeGoldPreview() {
    ArtaTheme {
        Content(
            destinations = walletDestinations("gold_savings"),
            onClickSelectWallet = {},
        )
    }
}

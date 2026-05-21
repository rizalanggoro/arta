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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.GoldCreateRoute
import id.my.rizalanggoro.arta.core.Routes.GoldTaxListRoute
import id.my.rizalanggoro.arta.core.Routes.HomeCashDashboardRoute
import id.my.rizalanggoro.arta.core.Routes.HomeGoldDashboardRoute
import id.my.rizalanggoro.arta.core.Routes.HomeGoldRoute
import id.my.rizalanggoro.arta.core.Routes.HomeSettingRoute
import id.my.rizalanggoro.arta.core.Routes.HomeTransactionRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionCreateRoute
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.HomeGoldDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldScreen
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionScreen
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeScreen(
    vm: HomeVM = viewModel(factory = HomeVM.Factory),
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
        selectedIndex = uiState.selectedIndex,
        onDestinationSelected = vm::onDestinationSelected,
        onClickSelectWallet = { backStack.add(WalletSelectRoute) },
        homeBackStack = homeBackStack,
        onClickFab = {
            when (walletType) {
                "cash_savings" -> backStack.add(TransactionCreateRoute)
                "gold_savings" -> backStack.add(GoldCreateRoute)
            }
        },
        entryProvider = entryProvider {
            entry<HomeCashDashboardRoute> { HomeCashDashboardScreen() }
            entry<HomeGoldDashboardRoute> { HomeGoldDashboardScreen() }
            entry<HomeTransactionRoute> { HomeTransactionScreen() }
            entry<HomeGoldRoute> {
                HomeGoldScreen(
                    onClickManageTax = { backStack.add(GoldTaxListRoute) },
                )
            }
            entry<HomeSettingRoute> { HomeSettingScreen() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    destinations: List<HomeDestination>,
    selectedIndex: Int,
    onDestinationSelected: (Int) -> Unit,
    onClickSelectWallet: () -> Unit,
    homeBackStack: (NavBackStack<NavKey>)? = null,
    entryProvider: ((NavKey) -> NavEntry<NavKey>)? = null,
    onClickFab: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Arta") },
                actions = {
                    FilledTonalIconButton(onClick = onClickSelectWallet) {
                        Icon(
                            Icons.Rounded.Wallet,
                            contentDescription = null
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (destinations.isNotEmpty() && homeBackStack != null)
                NavigationBar {
                    destinations.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = {
                                onDestinationSelected(index)
                                homeBackStack.removeFirstOrNull()
                                homeBackStack.add(destination.route)
                            },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(destination.label) },
                        )
                    }
                }
        },
        floatingActionButton = {
            if (selectedIndex in 0..1) {
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
    val route: NavKey
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
            selectedIndex = 0,
            onDestinationSelected = {},
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
            selectedIndex = 0,
            onDestinationSelected = {},
            onClickSelectWallet = {},
        )
    }
}

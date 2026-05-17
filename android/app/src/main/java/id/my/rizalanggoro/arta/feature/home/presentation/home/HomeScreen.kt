package id.my.rizalanggoro.arta.feature.home.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import id.my.rizalanggoro.arta.core.Routes.DashboardRoute
import id.my.rizalanggoro.arta.core.Routes.GoldRoute
import id.my.rizalanggoro.arta.core.Routes.SettingsRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionListRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeScreen(
    walletType: HomeWalletType = HomeWalletType.CashSavings,
    vm: HomeVM = viewModel(factory = HomeVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val destinations = walletDestinations(uiState.walletType)

    LaunchedEffect(walletType) {
        vm.onWalletTypeChanged(walletType)
    }

    // nested back stack for home children
    val homeBackStack = rememberNavBackStack(DashboardRoute) // Make nestedBackStack optional

    Content(
        walletType = uiState.walletType,
        destinations = destinations,
        selectedIndex = uiState.selectedIndex,
        onDestinationSelected = vm::onDestinationSelected,
        homeBackStack = homeBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    walletType: HomeWalletType,
    destinations: List<HomeDestination>,
    selectedIndex: Int,
    onDestinationSelected: (Int) -> Unit,
    homeBackStack: NavBackStack<NavKey> = rememberNavBackStack(DashboardRoute)
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Arta") },
                actions = {
                    FilledTonalIconButton(onClick = { }) {
                        Icon(
                            Icons.Rounded.Wallet,
                            contentDescription = null
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            onDestinationSelected(index)
                            // navigate nested backstack
                            val route = when (walletType) {
                                HomeWalletType.CashSavings -> when (index) {
                                    0 -> DashboardRoute
                                    1 -> TransactionListRoute
                                    2 -> TransactionListRoute
                                    else -> SettingsRoute
                                }

                                HomeWalletType.GoldSavings -> when (index) {
                                    0 -> GoldRoute
                                    1 -> GoldRoute
                                    else -> SettingsRoute
                                }
                            }
//                            nested.removeFirstOrNull()
//                            nested.add(route)
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
//            NavDisplay(
//                backStack = homeBackStack,
//                entryDecorators = listOf(
//                    rememberSaveableStateHolderNavEntryDecorator(),
//                    rememberViewModelStoreNavEntryDecorator(),
//                ),
//                entryProvider = entryProvider {
//                    entry<DashboardRoute> { HomeCashDashboardScreen() }
//                    entry<TransactionListRoute> { HomeTransactionListScreen() }
//                    entry<GoldRoute> {
//                        if (selectedIndex == 0) {
//                            HomeGoldDashboardScreen()
//                        } else {
//                            HomeGoldListContent()
//                        }
//                    }
//                    entry<SettingsRoute> { HomeSettingScreen() }
//                }
//            )
        }
    }
}

private data class HomeDestination(
    val label: String,
    val icon: ImageVector,
)

private fun walletDestinations(walletType: HomeWalletType): List<HomeDestination> {
    return when (walletType) {
        HomeWalletType.CashSavings -> listOf(
            HomeDestination(
                label = "Ringkasan",
                icon = Icons.Rounded.Dashboard,
            ),
            HomeDestination(
                label = "Transaksi",
                icon = Icons.Rounded.Payment,
            ),
//            HomeDestination(
//                label = "Chart",
//                icon = "3",
//            ),
            HomeDestination(
                label = "Pengaturan",
                icon = Icons.Rounded.Settings,
            ),
        )

        HomeWalletType.GoldSavings -> listOf(
            HomeDestination(
                label = "Ringkasan",
                icon = Icons.Rounded.Dashboard,
            ),
            HomeDestination(
                label = "Emas",
                icon = Icons.Rounded.Balance,
            ),
            HomeDestination(
                label = "Pengaturan",
                icon = Icons.Rounded.Settings,
            ),
        )
    }
}

@Preview(showBackground = true, name = "Cash Wallet Home")
@Composable
private fun HomeCashPreview() {
    ArtaTheme {
        Content(
            walletType = HomeWalletType.CashSavings,
            destinations = walletDestinations(HomeWalletType.CashSavings),
            selectedIndex = 0,
            onDestinationSelected = {},
        )
    }
}

@Preview(showBackground = true, name = "Gold Wallet Home")
@Composable
private fun HomeGoldPreview() {
    ArtaTheme {
        Content(
            walletType = HomeWalletType.GoldSavings,
            destinations = walletDestinations(HomeWalletType.GoldSavings),
            selectedIndex = 0,
            onDestinationSelected = {},
        )
    }
}

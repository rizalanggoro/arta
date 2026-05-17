package id.my.rizalanggoro.arta.feature.home.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.DashboardRoute
import id.my.rizalanggoro.arta.core.Routes.GoldRoute
import id.my.rizalanggoro.arta.core.Routes.SettingsRoute
import id.my.rizalanggoro.arta.core.Routes.TransactionListRoute
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.HomeGoldDashboardScreen
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldListContent
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingScreen
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionListScreen
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute

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
    val nestedBackStack = rememberNavBackStack(DashboardRoute) // Make nestedBackStack optional

    Content(
        walletType = uiState.walletType,
        destinations = destinations,
        selectedIndex = uiState.selectedIndex,
        onDestinationSelected = vm::onDestinationSelected,
        nestedBackStack = nestedBackStack, // Use internal nested variable
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    walletType: HomeWalletType,
    destinations: List<HomeDestination>,
    selectedIndex: Int,
    onDestinationSelected: (Int) -> Unit,
    nestedBackStack: androidx.navigation3.runtime.NavBackStack<androidx.navigation3.runtime.NavKey>? = null,
) {
    val nested = nestedBackStack ?: rememberNavBackStack(DashboardRoute)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Arta") },
                actions = {
                    val backStack = nested
                    TextButton(onClick = { backStack.add(WalletSelectRoute) }) {
                        Text(text = "Pilih Wallet")
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
                            nested.removeFirstOrNull()
                            nested.add(route)
                        },
                        icon = { Text(destination.icon) },
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
            CompositionLocalProvider(LocalBackStack provides nested) {
                NavDisplay(
                    backStack = nested,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry<DashboardRoute> { HomeCashDashboardScreen() }
                        entry<TransactionListRoute> { HomeTransactionListScreen() }
                        entry<GoldRoute> {
                            if (selectedIndex == 0) {
                                HomeGoldDashboardScreen()
                            } else {
                                HomeGoldListContent()
                            }
                        }
                        entry<SettingsRoute> { HomeSettingScreen() }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Destinasi tersedia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${destinations.size} menu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private data class HomeDestination(
    val label: String,
    val description: String,
    val icon: String,
)

private fun walletDestinations(walletType: HomeWalletType): List<HomeDestination> {
    return when (walletType) {
        HomeWalletType.CashSavings -> listOf(
            HomeDestination(
                label = "Dashboard",
                description = "Ringkasan keuangan, saldo, income, expense, dan chart transaksi.",
                icon = "1",
            ),
            HomeDestination(
                label = "Transaksi",
                description = "Daftar semua transaksi pada wallet tabungan uang.",
                icon = "2",
            ),
            HomeDestination(
                label = "Chart",
                description = "Visualisasi transaksi dan pola pengeluaran.",
                icon = "3",
            ),
            HomeDestination(
                label = "Setting",
                description = "Pengaturan akun, wallet, dan preferensi aplikasi.",
                icon = "4",
            ),
        )

        HomeWalletType.GoldSavings -> listOf(
            HomeDestination(
                label = "Dashboard",
                description = "Ringkasan aset emas, total gram, dan nilai emas.",
                icon = "1",
            ),
            HomeDestination(
                label = "Emas",
                description = "Daftar semua data emas pada wallet tabungan emas.",
                icon = "2",
            ),
            HomeDestination(
                label = "Setting",
                description = "Pengaturan akun, wallet, dan preferensi aplikasi.",
                icon = "3",
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

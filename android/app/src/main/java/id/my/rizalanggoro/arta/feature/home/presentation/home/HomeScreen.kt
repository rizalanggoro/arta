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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import id.my.rizalanggoro.arta.core.Routes.WalletRoute
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash.HomeCashDashboardContent
import id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold.HomeGoldDashboardContent
import id.my.rizalanggoro.arta.feature.home.presentation.transaction.HomeTransactionListContent
import id.my.rizalanggoro.arta.feature.home.presentation.gold.HomeGoldListContent
import id.my.rizalanggoro.arta.feature.home.presentation.chart.HomeChartContent
import id.my.rizalanggoro.arta.feature.home.presentation.setting.HomeSettingContent

@Composable
fun HomeScreen(
	walletType: HomeWalletType = HomeWalletType.CashSavings,
	vm: HomeVM = viewModel(factory = HomeVM.Factory),
) {
	val uiState by vm.uiState.collectAsState()
	val destinations = walletDestinations(uiState.walletType)
	val backStack = LocalBackStack.current
	val context = LocalContext.current

	LaunchedEffect(walletType) {
		vm.onWalletTypeChanged(walletType)
	}

	Content(
		walletType = uiState.walletType,
		destinations = destinations,
		selectedIndex = uiState.selectedIndex,
		onDestinationSelected = vm::onDestinationSelected,
		onClickManageWallet = { backStack.add(WalletRoute) },
		onClickManageCategory = { backStack.add(id.my.rizalanggoro.arta.core.Routes.CategoryRoute) },
		onLogout = {
			// clear session and navigate to auth
			val app = context.applicationContext as? id.my.rizalanggoro.arta.core.application.MyApplication
			app?.authPrefs?.clear()
			backStack.add(id.my.rizalanggoro.arta.core.Routes.AuthRoute)
		},
		onToggleTheme = { /* no-op: theme persistence not implemented */ },
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
	walletType: HomeWalletType,
	destinations: List<HomeDestination>,
	selectedIndex: Int,
	onDestinationSelected: (Int) -> Unit,
	onClickManageWallet: () -> Unit,
	onClickManageCategory: () -> Unit,
	onLogout: () -> Unit,
	onToggleTheme: (Boolean) -> Unit,
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = "Beranda") },
			)
		},
		bottomBar = {
			NavigationBar {
				destinations.forEachIndexed { index, destination ->
					NavigationBarItem(
						selected = selectedIndex == index,
						onClick = { onDestinationSelected(index) },
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
				.padding(paddingValues)
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = if (walletType == HomeWalletType.CashSavings) {
					"Wallet tabungan uang"
				} else {
					"Wallet tabungan emas"
				},
				style = MaterialTheme.typography.titleLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			when (walletType) {
				HomeWalletType.CashSavings -> when (selectedIndex) {
					0 -> HomeCashDashboardContent()
					1 -> HomeTransactionListContent()
					2 -> HomeChartContent()
					else -> HomeSettingContent(
						walletType = walletType,
						isDarkTheme = false,
						onToggleTheme = onToggleTheme,
						onLogout = onLogout,
						onManageCategory = onClickManageCategory,
						onClickManageWallet = onClickManageWallet,
					)
				}

				HomeWalletType.GoldSavings -> when (selectedIndex) {
					0 -> HomeGoldDashboardContent()
					1 -> HomeGoldListContent()
					else -> HomeSettingContent(
						walletType = walletType,
						isDarkTheme = false,
						onToggleTheme = onToggleTheme,
						onLogout = onLogout,
						onManageCategory = onClickManageCategory,
						onClickManageWallet = onClickManageWallet,
					)
				}
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
			onClickManageWallet = {},
			onClickManageCategory = {},
			onLogout = {},
			onToggleTheme = {},
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
			onClickManageWallet = {},
			onClickManageCategory = {},
			onLogout = {},
			onToggleTheme = {},
		)
	}
}

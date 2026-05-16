package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.WalletUpdateRoute
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ListWalletScreen(vm: ListWalletVM = viewModel(factory = ListWalletVM.Factory)) {
	val uiState by vm.uiState.collectAsState()
	val backStack = LocalBackStack.current

	LaunchedEffect(Unit) {
		vm.loadWallets()
	}

	Content(
		wallets = uiState.wallets,
		isLoading = uiState.isLoading,
		errorMessage = uiState.errorMessage,
		deleteTarget = uiState.deleteTarget,
		onClickEdit = { walletId -> backStack.add(WalletUpdateRoute(walletId = walletId)) },
		onClickDelete = vm::onDeleteRequested,
		onDismissDelete = vm::dismissDeleteDialog,
		onConfirmDelete = vm::confirmDeleteWallet,
		onRetry = vm::loadWallets,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
	wallets: List<Wallet> = emptyList(),
	isLoading: Boolean = false,
	errorMessage: String? = null,
	deleteTarget: Wallet? = null,
	onClickEdit: (Int) -> Unit = {},
	onClickDelete: (Wallet) -> Unit = {},
	onDismissDelete: () -> Unit = {},
	onConfirmDelete: (Wallet) -> Unit = {},
	onRetry: () -> Unit = {},
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Wallet") },
			)
		},
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Text(
				text = "Kelola daftar wallet, ubah detailnya, atau hapus wallet yang tidak dipakai.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			when {
				isLoading -> {
					Box(
						modifier = Modifier.fillMaxSize(),
						contentAlignment = Alignment.Center,
					) {
						CircularProgressIndicator()
					}
				}

				errorMessage != null -> {
					Card(modifier = Modifier.fillMaxWidth()) {
						Column(
							modifier = Modifier.padding(16.dp),
							verticalArrangement = Arrangement.spacedBy(12.dp),
						) {
							Text(text = errorMessage, style = MaterialTheme.typography.bodyLarge)
							Button(onClick = onRetry) {
								Text("Muat ulang")
							}
						}
					}
				}

				wallets.isEmpty() -> {
					Card(modifier = Modifier.fillMaxWidth()) {
						Column(
							modifier = Modifier.padding(16.dp),
							verticalArrangement = Arrangement.spacedBy(12.dp),
						) {
							Text(text = "Belum ada wallet.", style = MaterialTheme.typography.titleMedium)
							Text(
								text = "Wallet akan muncul di sini setelah dibuat dari backend.",
								color = MaterialTheme.colorScheme.onSurfaceVariant,
							)
							Button(onClick = onRetry) {
								Text("Cek lagi")
							}
						}
					}
				}

				else -> {
					LazyColumn(
						modifier = Modifier.fillMaxSize(),
						verticalArrangement = Arrangement.spacedBy(12.dp),
					) {
						items(wallets, key = { it.id }) { wallet ->
							WalletCard(
								wallet = wallet,
								onClickEdit = onClickEdit,
								onClickDelete = onClickDelete,
							)
						}
					}
				}
			}
		}
	}

	if (deleteTarget != null) {
		AlertDialog(
			onDismissRequest = onDismissDelete,
			title = { Text("Hapus wallet?") },
			text = { Text("Wallet \"${deleteTarget.name}\" akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.") },
			confirmButton = {
				Button(onClick = { onConfirmDelete(deleteTarget) }) {
					Text("Hapus")
				}
			},
			dismissButton = {
				OutlinedButton(onClick = onDismissDelete) {
					Text("Batal")
				}
			},
		)
	}
}

@Composable
private fun WalletCard(
	wallet: Wallet,
	onClickEdit: (Int) -> Unit,
	onClickDelete: (Wallet) -> Unit,
) {
	Card(modifier = Modifier.fillMaxWidth()) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
			) {
				Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
					Text(text = wallet.name, style = MaterialTheme.typography.titleMedium)
					Text(
						text = walletTypeLabel(wallet.type),
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}

				AssistChip(
					onClick = {},
					label = { Text(if (wallet.isDefault) "Default" else "Custom") },
				)
			}

			Text(
				text = "ID wallet: ${wallet.id}",
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				FilledTonalButton(
					onClick = { onClickEdit(wallet.id) },
					modifier = Modifier.weight(1f),
				) {
					Text("Ubah")
				}
				OutlinedButton(
					onClick = { onClickDelete(wallet) },
					modifier = Modifier.weight(1f),
				) {
					Text("Hapus")
				}
			}
		}
	}
}

private fun walletTypeLabel(type: String): String {
	return when (type) {
		"cash_savings" -> "Tabungan Uang"
		"gold_savings" -> "Tabungan Emas"
		else -> type.replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
	}
}

@Preview(showBackground = true, name = "Wallet List")
@Composable
private fun WalletListPreview() {
	ArtaTheme {
		Content(
			wallets = listOf(
				Wallet(
					id = 1,
					userId = 10,
					name = "Utama",
					type = "cash_savings",
					isDefault = true,
				),
				Wallet(
					id = 2,
					userId = 10,
					name = "Emas",
					type = "gold_savings",
				),
			),
		)
	}
}

package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.GoldTaxCreateRoute
import id.my.rizalanggoro.arta.core.Routes.GoldTaxUpdateRoute
import id.my.rizalanggoro.arta.domain.GoldTaxPreference
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ListGoldTaxScreen(
	vm: ListGoldTaxVM = viewModel(factory = ListGoldTaxVM.Factory),
) {
	val uiState by vm.uiState.collectAsState()
	val backStack = LocalBackStack.current
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		vm.loadTaxPreferences()
	}

	Content(
		snackbarHostState = snackbarHostState,
		preferences = uiState.preferences,
		deleteTarget = uiState.deleteTarget,
		isLoading = uiState.isLoading,
		errorMessage = uiState.errorMessage,
		onClickCreate = { backStack.add(GoldTaxCreateRoute) },
		onClickEdit = { taxPreferenceId -> backStack.add(GoldTaxUpdateRoute(taxPreferenceId = taxPreferenceId)) },
		onClickDelete = vm::onDeleteRequested,
		onClickBack = { backStack.removeLastOrNull() },
		onDismissDelete = vm::dismissDeleteDialog,
		onConfirmDelete = vm::confirmDeleteTaxPreference,
		onRetry = vm::loadTaxPreferences,
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
	snackbarHostState: SnackbarHostState,
	preferences: List<GoldTaxPreference> = emptyList(),
	deleteTarget: GoldTaxPreference? = null,
	isLoading: Boolean = false,
	errorMessage: String? = null,
	onClickCreate: () -> Unit = {},
	onClickEdit: (Int) -> Unit = {},
	onClickDelete: (GoldTaxPreference) -> Unit = {},
	onClickBack: () -> Unit = {},
	onDismissDelete: () -> Unit = {},
	onConfirmDelete: (GoldTaxPreference) -> Unit = {},
	onRetry: () -> Unit = {},
) {
	Scaffold(
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
		topBar = {
			TopAppBar(
				title = { Text("Pajak emas") },
				navigationIcon = {
					IconButton(onClick = onClickBack) {
						Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
					}
				},
			)
		},
	) { paddingValues ->
		when {
			isLoading -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues),
				contentAlignment = Alignment.Center,
			) {
				LoadingIndicator()
			}

			errorMessage != null -> Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				Text(text = errorMessage, style = MaterialTheme.typography.bodyMedium)
				OutlinedButton(onClick = onRetry) {
					Text("Coba lagi")
				}
			}

			else -> LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				item {
					OutlinedButton(
						onClick = onClickCreate,
						modifier = Modifier.fillMaxWidth(),
					) {
						Icon(Icons.Rounded.Add, contentDescription = null)
						Text("Tambah pajak baru")
					}
				}

				if (preferences.isEmpty()) {
					item {
						Column(
							modifier = Modifier
								.fillMaxWidth()
								.padding(top = 32.dp),
							horizontalAlignment = Alignment.CenterHorizontally,
							verticalArrangement = Arrangement.spacedBy(8.dp),
						) {
							Icon(
								Icons.Rounded.Inbox,
								contentDescription = null,
								tint = MaterialTheme.colorScheme.outlineVariant,
							)
							Text(
								text = "Belum ada preferensi pajak",
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.outline,
							)
						}
					}
				} else {
					items(preferences) { preference ->
						Card(modifier = Modifier.fillMaxWidth()) {
							Column(
								modifier = Modifier.padding(16.dp),
								verticalArrangement = Arrangement.spacedBy(12.dp),
							) {
								Text(
									text = "Karat ${preference.carat}",
									style = MaterialTheme.typography.titleMedium,
								)
								Text(
									text = "Rasio pajak ${preference.taxRate}%",
									style = MaterialTheme.typography.bodyMedium,
									color = MaterialTheme.colorScheme.outline,
								)
								OutlinedButton(
									onClick = { onClickEdit(preference.id) },
									modifier = Modifier.fillMaxWidth(),
								) {
									Icon(Icons.Rounded.Edit, contentDescription = null)
									Text("Ubah")
								}
								OutlinedButton(
									onClick = { onClickDelete(preference) },
									modifier = Modifier.fillMaxWidth(),
								) {
									Icon(Icons.Rounded.Delete, contentDescription = null)
									Text("Hapus")
								}
							}
						}
					}
				}
			}
		}
	}

	deleteTarget?.let { preference ->
		AlertDialog(
			onDismissRequest = onDismissDelete,
			title = { Text("Hapus preferensi karat?") },
			text = { Text("Karat ${preference.carat} dengan rasio pajak ${preference.taxRate}% akan dihapus.") },
			confirmButton = {
				TextButton(onClick = { onConfirmDelete(preference) }) {
					Text("Hapus")
				}
			},
			dismissButton = {
				TextButton(onClick = onDismissDelete) {
					Text("Batal")
				}
			},
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun PreviewListGoldTaxScreen() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
			preferences = listOf(
				GoldTaxPreference(id = 1, carat = 24.0, taxRate = 5.0),
				GoldTaxPreference(id = 2, carat = 18.0, taxRate = 3.5),
			),
		)
	}
}
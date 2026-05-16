package id.my.rizalanggoro.arta.feature.wallet.presentation.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UpdateWalletScreen(
	walletId: Int,
	vm: UpdateWalletVM = viewModel(factory = UpdateWalletVM.Factory),
) {
	val uiState by vm.uiState.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }
	val backStack = LocalBackStack.current

	LaunchedEffect(walletId) {
		vm.loadWallet(walletId)
	}

	LaunchedEffect(Unit) {
		vm.effect.collect { effect ->
			when (effect) {
				is UpdateWalletEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
				UpdateWalletEffect.NavigateBack -> backStack.removeLastOrNull()
			}
		}
	}

	Content(
		snackbarHostState = snackbarHostState,
		name = uiState.name,
		type = uiState.type,
		isDefault = uiState.isDefault,
		nameError = uiState.nameError,
		typeError = uiState.typeError,
		errorMessage = uiState.errorMessage,
		isLoading = uiState.isLoading,
		onChangeName = vm::onChangeName,
		onChangeType = vm::onChangeType,
		onToggleDefault = vm::onToggleDefault,
		onClickSubmit = vm::updateWallet,
		onClickBack = { backStack.removeLastOrNull() },
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
	snackbarHostState: SnackbarHostState,
	name: String = "",
	type: String = "cash_savings",
	isDefault: Boolean = false,
	nameError: String? = null,
	typeError: String? = null,
	errorMessage: String? = null,
	isLoading: Boolean = false,
	onChangeName: (String) -> Unit = {},
	onChangeType: (String) -> Unit = {},
	onToggleDefault: (Boolean) -> Unit = {},
	onClickSubmit: () -> Unit = {},
	onClickBack: () -> Unit = {},
) {
	val typeOptions = listOf(
		WalletTypeOption("Tabungan Uang", "cash_savings"),
		WalletTypeOption("Tabungan Emas", "gold_savings"),
	)

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Ubah Wallet") },
				navigationIcon = {
					TextButton(onClick = onClickBack) {
						Text("Batal")
					}
				},
			)
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Text(
				text = "Perbarui nama dan tipe wallet.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			if (errorMessage != null) {
				Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
			}

			OutlinedTextField(
				value = name,
				onValueChange = onChangeName,
				label = { Text("Nama wallet") },
				modifier = Modifier.fillMaxWidth(),
				isError = nameError != null,
				supportingText = {
					if (nameError != null) Text(nameError)
				},
				enabled = !isLoading,
				singleLine = true,
			)

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(text = "Tipe wallet", style = MaterialTheme.typography.labelLarge)
				typeOptions.forEach { option ->
					Button(
						onClick = { onChangeType(option.value) },
						enabled = !isLoading,
					) {
						Text(option.label + if (type == option.value) " ✓" else "")
					}
				}
				if (typeError != null) {
					Text(text = typeError, color = MaterialTheme.colorScheme.error)
				}
			}

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(text = "Wallet default", style = MaterialTheme.typography.labelLarge)
				Button(
					onClick = { onToggleDefault(!isDefault) },
					enabled = !isLoading,
				) {
					Text(if (isDefault) "Jadikan non-default" else "Jadikan default")
				}
			}

			Button(
				onClick = onClickSubmit,
				modifier = Modifier.fillMaxWidth(),
				enabled = !isLoading,
			) {
				if (isLoading) {
					CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
				} else {
					Text("Simpan Perubahan")
				}
			}
		}
	}
}

private data class WalletTypeOption(
	val label: String,
	val value: String,
)

@Preview(showBackground = true, name = "Update Wallet")
@Composable
private fun UpdateWalletPreview() {
	ArtaTheme {
		Content(snackbarHostState = remember { SnackbarHostState() })
	}
}

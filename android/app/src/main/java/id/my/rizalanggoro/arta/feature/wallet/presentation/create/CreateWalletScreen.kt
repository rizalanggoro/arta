package id.my.rizalanggoro.arta.feature.wallet.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(vm: CreateWalletVM = viewModel(factory = CreateWalletVM.Factory)) {
	val uiState by vm.uiState.collectAsState()
	val backStack = id.my.rizalanggoro.arta.core.LocalBackStack.current

	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		vm.messageEvent.collect { message ->
			snackbarHostState.showSnackbar(message)
		}
	}

	LaunchedEffect(Unit) {
		vm.effect.collect { effect ->
			when (effect) {
				is id.my.rizalanggoro.arta.feature.wallet.presentation.create.CreateWalletEffect.NavigateBack -> {
					backStack.removeLastOrNull()
				}
			}
		}
	}

	Content(
		snackbarHostState = snackbarHostState,
		isLoading = uiState.isLoading,
		name = uiState.name,
		type = uiState.type,
		nameError = uiState.nameError,
		typeError = uiState.typeError,
		onChangeName = vm::onChangeName,
		onChangeType = vm::onChangeType,
		onClickSubmit = vm::create,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
	snackbarHostState: SnackbarHostState,
	isLoading: Boolean = false,
	name: String = "",
	type: String = "",
	nameError: String? = null,
	typeError: String? = null,
	onChangeName: (String) -> Unit = {},
	onChangeType: (String) -> Unit = {},
	onClickSubmit: () -> Unit = {},
) {
	Scaffold(
		topBar = {
			TopAppBar(title = { Text("Buat Wallet") })
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(text = "Buat dompet baru", style = MaterialTheme.typography.headlineSmall)

			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				OutlinedTextField(
					value = name,
					onValueChange = onChangeName,
					label = { Text("Nama dompet") },
					modifier = Modifier.fillMaxWidth(),
					isError = nameError != null,
					supportingText = { if (nameError != null) Text(nameError) },
					enabled = !isLoading,
					singleLine = true,
				)

				OutlinedTextField(
					value = type,
					onValueChange = onChangeType,
					label = { Text("Tipe") },
					modifier = Modifier.fillMaxWidth(),
					isError = typeError != null,
					supportingText = { if (typeError != null) Text(typeError) },
					enabled = !isLoading,
					singleLine = true,
				)

				// default wallet option removed; selection persisted via global prefs
			}

			when (isLoading) {
				true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
				false -> Button(
					onClick = onClickSubmit,
					modifier = Modifier.fillMaxWidth(),
					enabled = !isLoading,
				) {
					Text("Buat")
				}
			}
		}
	}
}

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateWalletPreview() {
	ArtaTheme {
		Content(snackbarHostState = remember { SnackbarHostState() })
	}
}

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateWalletLoadingPreview() {
	ArtaTheme {
		Content(snackbarHostState = remember { SnackbarHostState() }, isLoading = true)
	}
}
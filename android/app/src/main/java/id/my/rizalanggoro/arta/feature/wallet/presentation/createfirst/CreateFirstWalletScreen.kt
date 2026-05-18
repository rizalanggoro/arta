package id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import id.my.rizalanggoro.arta.core.Routes.HomeRoute
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFirstWalletScreen(vm: CreateFirstWalletVM = viewModel(factory = CreateFirstWalletVM.Factory)) {
	val uiState by vm.uiState.collectAsState()
	val backStack = LocalBackStack.current

	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		vm.messageEvent.collect { message ->
			snackbarHostState.showSnackbar(message)
		}
	}

	LaunchedEffect(Unit) {
		vm.effect.collect { effect ->
			when (effect) {
				CreateFirstWalletEffect.NavigateHome -> {
					backStack.clear()
					backStack.add(HomeRoute)
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
			TopAppBar(title = { Text("Buat Wallet Pertama") })
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = "Wallet ini akan langsung dijadikan wallet aktif untuk akun baru Anda.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				TextField(
					value = name,
					onValueChange = onChangeName,
					label = { Text("Nama dompet") },
					modifier = Modifier.fillMaxWidth(),
					isError = nameError != null,
					supportingText = when {
						nameError != null -> { { Text(nameError) } }
						else -> null
					},
					enabled = !isLoading,
					singleLine = true,
				)

				TextField(
					value = type,
					onValueChange = onChangeType,
					label = { Text("Tipe") },
					modifier = Modifier.fillMaxWidth(),
					isError = typeError != null,
					supportingText = when {
						typeError != null -> { { Text(typeError) } }
						else -> null
					},
					enabled = !isLoading,
					singleLine = true,
				)
			}

			Button(
				onClick = onClickSubmit,
				modifier = Modifier.fillMaxWidth(),
				enabled = !isLoading,
			) {
				Text("Lanjut ke Home")
			}
		}
	}
}

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateFirstWalletPreview() {
	ArtaTheme {
		Content(snackbarHostState = remember { SnackbarHostState() })
	}
}

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateFirstWalletLoadingPreview() {
	ArtaTheme {
		Content(snackbarHostState = remember { SnackbarHostState() }, isLoading = true)
	}
}
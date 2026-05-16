package id.my.rizalanggoro.arta.feature.gold.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateGoldScreen(
	walletId: Int? = null,
	vm: CreateGoldVM = viewModel(factory = CreateGoldVM.Factory),
) {
	val uiState by vm.uiState.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }
	val backStack = LocalBackStack.current

	LaunchedEffect(walletId) {
		vm.onWalletIdPrefilled(walletId)
	}

	LaunchedEffect(Unit) {
		vm.effect.collect { effect ->
			when (effect) {
				is CreateGoldEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
				CreateGoldEffect.NavigateBack -> backStack.removeLastOrNull()
			}
		}
	}

	Content(
		snackbarHostState = snackbarHostState,
		walletId = uiState.walletId,
		date = uiState.date,
		grams = uiState.grams,
		pricePerGram = uiState.pricePerGram,
		type = uiState.type,
		purityPercent = uiState.purityPercent,
		notes = uiState.notes,
		walletIdError = uiState.walletIdError,
		dateError = uiState.dateError,
		gramsError = uiState.gramsError,
		pricePerGramError = uiState.pricePerGramError,
		purityPercentError = uiState.purityPercentError,
		isLoading = uiState.isLoading,
		onWalletIdChanged = vm::onWalletIdChanged,
		onDateChanged = vm::onDateChanged,
		onGramsChanged = vm::onGramsChanged,
		onPricePerGramChanged = vm::onPricePerGramChanged,
		onTypeChanged = vm::onTypeChanged,
		onPurityPercentChanged = vm::onPurityPercentChanged,
		onNotesChanged = vm::onNotesChanged,
		onClickSave = vm::createGold,
		onClickBack = { backStack.removeLastOrNull() },
	)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
	snackbarHostState: SnackbarHostState,
	walletId: String = "",
	date: String = "",
	grams: String = "",
	pricePerGram: String = "",
	type: String = "pure_gold",
	purityPercent: String = "99.9",
	notes: String = "",
	walletIdError: String? = null,
	dateError: String? = null,
	gramsError: String? = null,
	pricePerGramError: String? = null,
	purityPercentError: String? = null,
	isLoading: Boolean = false,
	onWalletIdChanged: (String) -> Unit = {},
	onDateChanged: (String) -> Unit = {},
	onGramsChanged: (String) -> Unit = {},
	onPricePerGramChanged: (String) -> Unit = {},
	onTypeChanged: (String) -> Unit = {},
	onPurityPercentChanged: (String) -> Unit = {},
	onNotesChanged: (String) -> Unit = {},
	onClickSave: () -> Unit = {},
	onClickBack: () -> Unit = {},
) {
	val typeOptions = listOf(
		GoldTypeOption("Emas Murni", "pure_gold"),
		GoldTypeOption("Perhiasan Emas", "gold_jewelry"),
		GoldTypeOption("Emas Investasi", "investment_gold"),
		GoldTypeOption("Lainnya", "other"),
	)

	val estimatedValue = runCatching {
		grams.toDouble() * pricePerGram.toDouble()
	}.getOrNull()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Buat Data Emas") },
				navigationIcon = {
					TextButton(onClick = onClickBack) {
						Text("Batal")
					}
				},
			)
		},
		bottomBar = {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(20.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Text(
					text = if (estimatedValue != null) {
						"Estimasi nilai: Rp ${String.format(Locale.US, "%,.0f", estimatedValue)}"
					} else {
						"Estimasi nilai akan tampil setelah gram dan harga diisi."
					},
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
				Button(
					onClick = onClickSave,
					modifier = Modifier.fillMaxWidth(),
					enabled = !isLoading,
				) {
					if (isLoading) {
						CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
					} else {
						Text("Simpan Emas")
					}
				}
			}
		},
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(paddingValues)
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Text(
				text = "Masukkan detail emas pada wallet tabungan emas.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			SnackbarHost(hostState = snackbarHostState)

			OutlinedTextField(
				value = walletId,
				onValueChange = onWalletIdChanged,
				label = { Text("Wallet ID") },
				modifier = Modifier.fillMaxWidth(),
				isError = walletIdError != null,
				supportingText = {
					if (walletIdError != null) Text(walletIdError)
				},
				enabled = !isLoading,
				singleLine = true,
			)

			OutlinedTextField(
				value = date,
				onValueChange = onDateChanged,
				label = { Text("Tanggal (ISO 8601)") },
				modifier = Modifier.fillMaxWidth(),
				isError = dateError != null,
				supportingText = {
					if (dateError != null) Text(dateError)
					else Text("Contoh: 2026-05-16T10:30:00+07:00")
				},
				enabled = !isLoading,
				singleLine = true,
			)

			OutlinedTextField(
				value = grams,
				onValueChange = onGramsChanged,
				label = { Text("Gram") },
				modifier = Modifier.fillMaxWidth(),
				isError = gramsError != null,
				supportingText = {
					if (gramsError != null) Text(gramsError)
				},
				enabled = !isLoading,
				singleLine = true,
			)

			OutlinedTextField(
				value = pricePerGram,
				onValueChange = onPricePerGramChanged,
				label = { Text("Harga per gram") },
				modifier = Modifier.fillMaxWidth(),
				isError = pricePerGramError != null,
				supportingText = {
					if (pricePerGramError != null) Text(pricePerGramError)
				},
				enabled = !isLoading,
				singleLine = true,
			)

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					text = "Tipe emas",
					style = MaterialTheme.typography.labelLarge,
				)
				Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
					typeOptions.forEach { option ->
						FilterChip(
							selected = type == option.value,
							onClick = { onTypeChanged(option.value) },
							label = { Text(option.label) },
							enabled = !isLoading,
						)
					}
				}
			}

			OutlinedTextField(
				value = purityPercent,
				onValueChange = onPurityPercentChanged,
				label = { Text("Persentase kemurnian") },
				modifier = Modifier.fillMaxWidth(),
				isError = purityPercentError != null,
				supportingText = {
					if (purityPercentError != null) Text(purityPercentError)
					else Text("Contoh: 99.9")
				},
				enabled = !isLoading,
				singleLine = true,
			)

			OutlinedTextField(
				value = notes,
				onValueChange = onNotesChanged,
				label = { Text("Catatan") },
				modifier = Modifier.fillMaxWidth(),
				enabled = !isLoading,
				minLines = 3,
			)
		}
	}
}

private data class GoldTypeOption(
	val label: String,
	val value: String,
)

@Preview(showBackground = true, name = "Create Gold - Default")
@Composable
private fun CreateGoldDefaultPreview() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
			walletId = "12",
			date = "2026-05-16T10:30:00+07:00",
			grams = "1.5",
			pricePerGram = "1200000",
		)
	}
}

@Preview(showBackground = true, name = "Create Gold - Loading")
@Composable
private fun CreateGoldLoadingPreview() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
			isLoading = true,
		)
	}
}

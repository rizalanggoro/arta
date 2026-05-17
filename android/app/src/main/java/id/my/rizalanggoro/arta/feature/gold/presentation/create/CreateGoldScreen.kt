package id.my.rizalanggoro.arta.feature.gold.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.app.DatePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateGoldScreen(
	vm: CreateGoldVM = viewModel(factory = CreateGoldVM.Factory),
) {
	val uiState by vm.uiState.collectAsState()
	val snackbarHostState = remember { SnackbarHostState() }
	val backStack = LocalBackStack.current

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
		price = uiState.price,
		type = uiState.type,
		purityPercent = uiState.purityPercent,
		notes = uiState.notes,
		walletIdError = uiState.walletIdError,
		dateError = uiState.dateError,
		gramsError = uiState.gramsError,
		priceError = uiState.priceError,
		purityPercentError = uiState.purityPercentError,
		isLoading = uiState.isLoading,
		onDateChanged = vm::onDateChanged,
		onGramsChanged = vm::onGramsChanged,
		onPriceChanged = vm::onPricePerGramChanged,
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
	price: String = "",
	type: String = "pure_gold",
	purityPercent: String = "99.9",
	notes: String = "",
	walletIdError: String? = null,
	dateError: String? = null,
	gramsError: String? = null,
	priceError: String? = null,
	purityPercentError: String? = null,
	isLoading: Boolean = false,
	onDateChanged: (String) -> Unit = {},
	onGramsChanged: (String) -> Unit = {},
	onPriceChanged: (String) -> Unit = {},
	onTypeChanged: (String) -> Unit = {},
	onPurityPercentChanged: (String) -> Unit = {},
	onNotesChanged: (String) -> Unit = {},
	onClickSave: () -> Unit = {},
	onClickBack: () -> Unit = {},
) {
	val typeOptions = listOf(
		GoldTypeOption("Emas Murni", "pure_gold"),
		GoldTypeOption("Perhiasan Emas", "gold_jewelry"),
	)
	val context = LocalContext.current
	val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
	val selectedDate = remember(date) {
		runCatching { ZonedDateTime.parse(date).toLocalDate() }
			.getOrNull()
			?: runCatching { LocalDate.parse(date.take(10)) }.getOrNull()
			?: LocalDate.now()
	}
	val datePicker = remember(date) {
		DatePickerDialog(
			context,
			{ _, year, month, dayOfMonth ->
				val localDate = LocalDate.of(year, month + 1, dayOfMonth)
				val zoned = localDate.atStartOfDay(ZoneId.systemDefault())
				onDateChanged(zoned.format(dateFormatter))
			},
			selectedDate.year,
			selectedDate.monthValue - 1,
			selectedDate.dayOfMonth,
		)
	}

	val estimatedValue = runCatching {
		grams.toDouble() * price.toDouble()
	}.getOrNull()

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Tambah Emas") },
				navigationIcon = {
					IconButton(onClick = onClickBack) {
						Icon(
							Icons.AutoMirrored.Rounded.ArrowBack,
							contentDescription = null)
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

			Card(modifier = Modifier.fillMaxWidth()) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp),
					horizontalAlignment = Alignment.Start,
				) {
					Text(
						text = "Wallet aktif",
						style = MaterialTheme.typography.labelLarge,
					)
					Text(
						text = if (walletId.isBlank()) "Belum ada wallet aktif" else "ID wallet: $walletId",
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
					if (walletIdError != null) {
						Text(walletIdError, color = MaterialTheme.colorScheme.error)
					}
				}
			}

			SnackbarHost(hostState = snackbarHostState)

			TextField(
				value = date,
				onValueChange = {},
				label = { Text("Tanggal (ISO 8601)") },
				modifier = Modifier.fillMaxWidth(),
				isError = dateError != null,
				supportingText = when {
					dateError != null -> {
						{ Text(dateError) }
					}
					else -> {
						{ Text("Contoh: 2026-05-16T10:30:00+07:00") }
					}
				},
				enabled = !isLoading,
				singleLine = true,
				readOnly = true,
				trailingIcon = {
					TextButton(onClick = { datePicker.show() }, enabled = !isLoading) {
						Text("Pilih")
					}
				},
			)

			TextField(
				value = grams,
				onValueChange = onGramsChanged,
				label = { Text("Gram") },
				modifier = Modifier.fillMaxWidth(),
				isError = gramsError != null,
				supportingText = when {
					gramsError != null -> {
						{ Text(gramsError) }
					}
					else -> null
				},
				enabled = !isLoading,
				singleLine = true,
			)

			TextField(
				value = price,
				onValueChange = onPriceChanged,
				label = { Text("Harga beli (total)") },
				modifier = Modifier.fillMaxWidth(),
				isError = priceError != null,
				supportingText = when {
					priceError != null -> {
						{ Text(priceError) }
					}
					else -> null
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

			TextField(
				value = purityPercent,
				onValueChange = onPurityPercentChanged,
				label = { Text("Persentase kemurnian") },
				modifier = Modifier.fillMaxWidth(),
				isError = purityPercentError != null,
				supportingText = when {
					purityPercentError != null -> {
						{ Text(purityPercentError) }
					}
					else -> {
						{ Text("Contoh: 99.9") }
					}
				},
				enabled = !isLoading,
				singleLine = true,
			)

			TextField(
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
			price = "1200000",
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

@Preview(showBackground = true, name = "Create Gold - Error")
@Composable
private fun CreateGoldErrorPreview() {
	ArtaTheme {
		Content(
			snackbarHostState = remember { SnackbarHostState() },
			walletIdError = "Wallet aktif belum dipilih",
			gramsError = "Gram tidak valid",
			priceError = "Harga tidak valid",
		)
	}
}

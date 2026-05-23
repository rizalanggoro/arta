package id.my.rizalanggoro.arta.feature.gold.presentation.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.app.DatePickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateGoldScreen(goldId: Int) {
    val viewModel: UpdateGoldVM = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(goldId) {
        viewModel.load(goldId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UpdateGoldEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                UpdateGoldEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        date = uiState.date,
        grams = uiState.grams,
        price = uiState.price,
        type = uiState.type,
        carat = uiState.carat,
        notes = uiState.notes,
        isLoading = uiState.isLoading,
        onDateChanged = viewModel::onDateChanged,
        onGramsChanged = viewModel::onGramsChanged,
        onPricePerGramChanged = viewModel::onPriceChanged,
        onTypeChanged = viewModel::onTypeChanged,
        onCaratChanged = viewModel::onCaratChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onClickSave = viewModel::updateGold,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState,
    date: String = "",
    grams: String = "",
    price: String = "",
    type: String = "",
    carat: String = "",
    notes: String = "",
    isLoading: Boolean = false,
    onDateChanged: (String) -> Unit = {},
    onGramsChanged: (String) -> Unit = {},
    onPricePerGramChanged: (String) -> Unit = {},
    onTypeChanged: (String) -> Unit = {},
    onCaratChanged: (String) -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onClickSave: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Ubah Emas") },
                navigationIcon = {
                    TextButton(onClick = onClickBack) {
                        Text(text = "Batal")
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
                Button(
                    onClick = onClickSave,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(text = "Simpan Perubahan")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Perbarui data emas yang sudah tersimpan.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            TextField(
                value = date,
                onValueChange = {},
                label = { Text(text = "Tanggal (ISO)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { datePicker.show() }, enabled = !isLoading) {
                        Text(text = "Pilih")
                    }
                },
            )
            TextField(
                value = grams,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) onGramsChanged(it) },
                label = { Text(text = "Gram") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !isLoading,
                singleLine = true,
            )
            TextField(
                value = price,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) onPricePerGramChanged(it) },
                label = { Text(text = "Harga beli (total)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !isLoading,
                singleLine = true,
            )
            TextField(
                value = type,
                onValueChange = onTypeChanged,
                label = { Text(text = "Tipe") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
            )
            TextField(
                value = carat,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) onCaratChanged(it) },
                label = { Text(text = "Karat") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !isLoading,
                singleLine = true,
            )
            TextField(
                value = notes,
                onValueChange = onNotesChanged,
                label = { Text(text = "Catatan") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                minLines = 3,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Update Gold - Default")
@Composable
private fun UpdateGoldScreenPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            date = "2026-05-16",
            grams = "10",
            price = "900000",
            type = "pure_gold",
            carat = "24.0",
            notes = "Contoh catatan",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Update Gold - Loading")
@Composable
private fun UpdateGoldLoadingPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = true,
        )
    }
}

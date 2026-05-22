package id.my.rizalanggoro.arta.feature.gold.presentation.create

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.constant.goldTypes
import id.my.rizalanggoro.arta.domain.Wallet
import id.my.rizalanggoro.arta.shared.component.MyDatePickerDialog
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
        selectedWallet = uiState.selectedWallet,
        date = uiState.date,
        grams = uiState.grams,
        price = uiState.price,
        type = uiState.type,
        carat = uiState.carat,
        notes = uiState.notes,
        dateError = uiState.dateError,
        gramsError = uiState.gramsError,
        priceError = uiState.priceError,
        caratError = uiState.caratError,
        isLoading = uiState.isLoading,
        onDateChanged = vm::onDateChanged,
        onGramsChanged = vm::onGramsChanged,
        onPriceChanged = vm::onPricePerGramChanged,
        onTypeChanged = vm::onTypeChanged,
        onCaratChanged = vm::onCaratChanged,
        onNotesChanged = vm::onNotesChanged,
        onClickSave = vm::createGold,
        onClickBack = { backStack.removeLastOrNull() },
    )

    if (false)
        MyDatePickerDialog(
            state = TODO(),
            onDismiss = TODO(),
            onDateSelected = TODO()
        )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    selectedWallet: Wallet? = null,
    date: String = "",
    grams: String = "",
    price: String = "",
    type: String = "pure_gold",
    carat: String = "24.0",
    notes: String = "",
    dateError: String? = null,
    gramsError: String? = null,
    priceError: String? = null,
    caratError: String? = null,
    isLoading: Boolean = false,
    onDateChanged: (String) -> Unit = {},
    onGramsChanged: (String) -> Unit = {},
    onPriceChanged: (String) -> Unit = {},
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
        topBar = {
            TopAppBar(
                title = { Text("Tambah Emas") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
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

            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Rounded.Wallet,
                        null
                    )
                },
                headlineContent = {
                    Text("Nama dompet")
                },
                supportingContent = {
                    Text("Jenis dompet")
                }
            )
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Rounded.Today,
                        null
                    )
                },
                headlineContent = {
                    Text("Tanggal")
                },
                supportingContent = {
                    Text("Pilih tanggal")
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null
                    )
                },
                modifier = Modifier.clickable(enabled = !isLoading) {

                }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                TextField(
                    value = grams,
                    onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) onGramsChanged(it) },
                    label = { Text("Berat (gram)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                    onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*$"))) onPriceChanged(it) },
                    label = { Text("Harga beli") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Tipe emas",
                    style = MaterialTheme.typography.titleSmall,
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    goldTypes.mapIndexed { index, item ->
                        SegmentedButton(
                            enabled = !isLoading,
                            selected = item.value == type,
                            onClick = { onTypeChanged(item.value) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = goldTypes.size
                            )
                        ) {
                            Text(item.name)
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                TextField(
                    value = carat,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) onCaratChanged(it)
                    },
                    label = { Text("Karat") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = caratError != null,
                    supportingText = when {
                        caratError != null -> {
                            { Text(caratError) }
                        }

                        else -> null
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

            when {
                isLoading -> LoadingIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                else -> Button(
                    onClick = onClickSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Create Gold - Default")
@Composable
private fun CreateGoldDefaultPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            selectedWallet = Wallet(
                id = 12,
                userID = 1,
                name = "Tabungan Emas",
                type = "gold_savings",
            ),
            date = "2026-05-16T10:30:00+07:00",
            grams = "1.5",
            price = "1200000",
            carat = "24.0",
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
            carat = "18.0",
        )
    }
}

@Preview(showBackground = true, name = "Create Gold - Error")
@Composable
private fun CreateGoldErrorPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            gramsError = "Gram tidak valid",
            priceError = "Harga tidak valid",
        )
    }
}

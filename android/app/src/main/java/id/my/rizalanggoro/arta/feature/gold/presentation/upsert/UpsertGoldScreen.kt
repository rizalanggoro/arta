package id.my.rizalanggoro.arta.feature.gold.presentation.upsert

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.constant.goldTypes
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.isValidInputNumber
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.shared.component.MyDatePickerDialog
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpsertGoldScreen(
    goldId: Int = 0,
    vm: UpsertGoldVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(goldId) {
        vm.setGoldId(goldId)
    }

    LaunchedEffect(Unit) {
        AppEventBus.event.filterIsInstance<AppEvent.GoldChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    LaunchedEffect(Unit) {
        vm.event.filterIsInstance<UpsertGoldUiState.Event.ShowMessage>()
            .collect { snackbarHostState.showSnackbar(it.message) }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onGramsChanged = vm::onGramsChanged,
        onPriceChanged = vm::onPriceChanged,
        onTypeChanged = vm::onTypeChanged,
        onCaratChanged = vm::onCaratChanged,
        onNotesChanged = vm::onNotesChanged,
        onClickBack = { backStack.removeLastOrNull() },
        onClickSelectDate = vm::onSelectDateClicked,
        onClickSubmit = vm::onSubmitClicked,
    )

    if (uiState.isDatePickerOpen)
        MyDatePickerDialog(
            onDismiss = vm::onDatePickerDismissed,
            onDateSelected = vm::onDateChanged
        )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    uiState: UpsertGoldUiState = UpsertGoldUiState(),
    onClickSelectDate: () -> Unit = {},
    onGramsChanged: (String) -> Unit = {},
    onPriceChanged: (String) -> Unit = {},
    onTypeChanged: (String) -> Unit = {},
    onCaratChanged: (String) -> Unit = {},
    onNotesChanged: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            uiState.isUpdate -> "Ubah Emas"
                            else -> "Tambah Emas"
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Wallet,
                            null
                        )
                    },
                    headlineContent = {
                        Text(uiState.selectedWallet?.name ?: "Tidak ada dompet")
                    },
                    supportingContent = {
                        Text(
                            uiState.selectedWallet?.type?.toWalletName()
                                ?: "Tidak ada jenis dompet"
                        )
                    },
                )

                ListItem(
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                        Text(uiState.date.toIndonesianDate())
                    },
                    trailingContent = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            null
                        )
                    },
                    modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                        onClickSelectDate()
                    },
                )
            }

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                goldTypes.mapIndexed { index, option ->
                    SegmentedButton(
                        colors = SegmentedButtonDefaults.colors(
                            activeBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        selected = uiState.type == option.value,
                        onClick = { onTypeChanged(option.value) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = goldTypes.size,
                        ),
                        enabled = !uiState.isLoading,
                    ) {
                        Text(option.name)
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = uiState.grams,
                    onValueChange = {
                        if (it.isValidInputNumber())
                            onGramsChanged(it)
                    },
                    label = { Text("Berat (gram)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = uiState.gramsError != null,
                    supportingText = when {
                        uiState.gramsError != null -> {
                            { Text(uiState.gramsError) }
                        }

                        else -> null
                    },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                )

                TextField(
                    value = uiState.price,
                    onValueChange = {
                        if (it.isValidInputNumber())
                            onPriceChanged(it)
                    },
                    label = { Text("Harga beli") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = uiState.priceError != null,
                    supportingText = when {
                        uiState.priceError != null -> {
                            { Text(uiState.priceError) }
                        }

                        else -> null
                    },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                )

                TextField(
                    value = uiState.carat,
                    onValueChange = {
                        if (it.isValidInputNumber())
                            onCaratChanged(it)
                    },
                    label = { Text("Karat") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = uiState.caratError != null,
                    supportingText = when {
                        uiState.caratError != null -> {
                            { Text(uiState.caratError) }
                        }

                        else -> null
                    },
                    enabled = !uiState.isLoading,
                    singleLine = true,
                )

                TextField(
                    value = uiState.notes,
                    onValueChange = onNotesChanged,
                    label = { Text("Catatan (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    minLines = 3,
                )
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }

                else -> Button(
                    onClick = onClickSubmit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreatePreview() {
    ArtaTheme {
        Content(
            uiState = UpsertGoldUiState(
                selectedWallet = DomainWallet(
                    createdAt = "2026-05-24T00:00:00Z",
                    id = 1,
                    name = "Dompet Emas",
                    type = "gold_savings",
                    updatedAt = "2026-05-24T00:00:00Z",
                    userId = 1,
                ),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdatePreview() {
    ArtaTheme {
        Content(
            uiState = UpsertGoldUiState(
                goldId = 10,
                isUpdate = true,
                date = System.currentTimeMillis(),
                grams = "10",
                price = "900000",
                type = "pure_gold",
                carat = "24.0",
                notes = "Contoh catatan",
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    ArtaTheme {
        Content(
            uiState = UpsertGoldUiState(
                goldId = 10,
                isUpdate = true,
                isLoading = true,
                date = System.currentTimeMillis(),
                grams = "10",
                price = "900000",
                type = "pure_gold",
                carat = "24.0",
                notes = "Contoh catatan",
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    ArtaTheme {
        Content(
            uiState = UpsertGoldUiState(
                selectedWallet = DomainWallet(
                    createdAt = "2026-05-24T00:00:00Z",
                    id = 1,
                    name = "Dompet Emas",
                    type = "gold_savings",
                    updatedAt = "2026-05-24T00:00:00Z",
                    userId = 1,
                ),
                gramsError = "Gram wajib berupa angka lebih dari 0",
            )
        )
    }
}

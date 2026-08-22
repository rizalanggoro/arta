package id.my.rizalanggoro.arta.feature.gold.presentation.upsert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.constant.goldTypes
import id.my.rizalanggoro.arta.core.constant.toWalletName
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.isValidInputNumber
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.MyDatePickerDialog
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ErrorColor = Color(0xFFE53935)

@Composable
fun UpsertGoldScreen(
    vm: UpsertGoldVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

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

@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
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
            SmallTopAppBar(
                title = when {
                    uiState.isUpdate -> "Ubah Emas"
                    else -> "Tambah Emas"
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
        snackbarHost = { SnackbarHost(state = snackbarHostState) },
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
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MiuixTheme.colorScheme.surfaceContainer)
            ) {
                BasicComponent(
                    title = uiState.selectedWallet?.name ?: "Tidak ada dompet",
                    summary = uiState.selectedWallet?.type?.toWalletName()
                        ?: "Tidak ada jenis dompet",
                    startAction = {
                        Icon(
                            Icons.Rounded.Wallet,
                            null
                        )
                    },
                )

                BasicComponent(
                    title = "Tanggal",
                    summary = uiState.date.toIndonesianDate(),
                    startAction = {
                        Icon(
                            Icons.Rounded.Today,
                            null
                        )
                    },
                    endActions = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            null
                        )
                    },
                    onClick = if (uiState.isLoading) ({}) else onClickSelectDate,
                )
            }

            TabRowWithContour(
                tabs = goldTypes.map { it.name },
                selectedTabIndex = goldTypes.indexOfFirst { it.value == uiState.type },
                onTabSelected = { index -> onTypeChanged(goldTypes[index].value) },
                modifier = Modifier.fillMaxWidth(),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    TextField(
                        value = uiState.grams,
                        onValueChange = {
                            if (it.isValidInputNumber())
                                onGramsChanged(it)
                        },
                        label = "Berat (gram)",
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        enabled = !uiState.isLoading,
                        singleLine = true,
                    )
                    uiState.gramsError?.let {
                        Text(it, fontSize = 13.sp, color = ErrorColor)
                    }
                }

                Column {
                    TextField(
                        value = uiState.price,
                        onValueChange = {
                            if (it.isValidInputNumber())
                                onPriceChanged(it)
                        },
                        label = "Harga beli",
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        enabled = !uiState.isLoading,
                        singleLine = true,
                    )
                    uiState.priceError?.let {
                        Text(it, fontSize = 13.sp, color = ErrorColor)
                    }
                }

                Column {
                    TextField(
                        value = uiState.carat,
                        onValueChange = {
                            if (it.isValidInputNumber())
                                onCaratChanged(it)
                        },
                        label = "Karat",
                        useLabelAsPlaceholder = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        enabled = !uiState.isLoading,
                        singleLine = true,
                    )
                    uiState.caratError?.let {
                        Text(it, fontSize = 13.sp, color = ErrorColor)
                    }
                }

                TextField(
                    value = uiState.notes,
                    onValueChange = onNotesChanged,
                    label = "Catatan (opsional)",
                    useLabelAsPlaceholder = true,
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
                    InfiniteProgressIndicator()
                }

                else -> Button(
                    onClick = onClickSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                    enabled = !uiState.isLoading
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
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

package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.isValidInputNumber
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.MyDatePickerDialog
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val ErrorColor = Color(0xFFE53935)

@Composable
fun UpsertTransactionScreen(
    vm: UpsertTransactionVM = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.date)

    LaunchedEffect(Unit) {
        AppEventBus.event.filterIsInstance<AppEvent.TransactionChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    LaunchedEffect(Unit) {
        vm.event.filterIsInstance<UpsertTransactionUiState.Event.ShowMessage>()
            .collect { snackbarHostState.showSnackbar(it.message) }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onAmountChanged = vm::onAmountChanged,
        onDescriptionChanged = vm::onDescriptionChanged,
        onClickSelectCategory = {
            backStack.add(
                CategoryRoute.Select(
                    categoryId = uiState.selectedCategory?.id
                )
            )
        },
        onClickSelectDate = vm::onSelectDateClicked,
        onClickSubmit = vm::onSubmitClicked,
        onClickBack = { backStack.removeLastOrNull() },
    )

    if (uiState.isDatePickerOpen) MyDatePickerDialog(
        state = datePickerState,
        onDismiss = vm::onDatePickerDismissed,
        onDateSelected = vm::onDateSelected
    )
}

@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    uiState: UpsertTransactionUiState = UpsertTransactionUiState(),
    onAmountChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onClickSelectCategory: () -> Unit = {},
    onClickSelectDate: () -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = when {
                    uiState.isUpdate -> "Ubah Transaksi"
                    else -> "Tambah Transaksi"
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            MiuixIcons.Back, null
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
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            Card(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                BasicComponent(
                    title = "Dompet",
                    summary = uiState.selectedWallet?.name ?: "Tidak ada dompet",
                    startAction = {
                        Icon(
                            Icons.Rounded.Wallet, null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                )
            }

            Card(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                ArrowPreference(
                    title = "Kategori",
                    summary = uiState.selectedCategory?.name ?: "Pilih kategori",
                    startAction = {
                        Icon(
                            Icons.Rounded.Category, null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                    onClick = onClickSelectCategory,
                )
                ArrowPreference(
                    title = "Tanggal",
                    summary = uiState.date.toIndonesianDate(),
                    startAction = {
                        Icon(
                            Icons.Rounded.Today, null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                    onClick = onClickSelectDate,
                )
            }

            Column {
                TextField(
                    value = uiState.amount,
                    onValueChange = {
                        if (it.isValidInputNumber()) onAmountChanged(it)
                    },
                    label = "Jumlah",
                    useLabelAsPlaceholder = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = !uiState.isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                uiState.amountError?.let {
                    Text(it, fontSize = 13.sp, color = ErrorColor)
                }
            }

            TextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                label = "Catatan (opsional)",
                useLabelAsPlaceholder = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = !uiState.isLoading,
                singleLine = false,
                minLines = 5,
            )

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    InfiniteProgressIndicator()
                }

                else -> Button(
                    onClick = onClickSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    colors = ButtonDefaults.buttonColorsPrimary(),
                    enabled = !uiState.isLoading
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Create Transaction")
@Composable
private fun CreateTransactionPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = UpsertTransactionUiState(
                amount = "50000",
            ),
        )
    }
}

@Preview(showBackground = true, name = "Update Transaction")
@Composable
private fun UpdateTransactionPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = UpsertTransactionUiState(
                transactionId = 10,
                isUpdate = true,
                amount = "50000",
                description = "Contoh transaksi",
                date = System.currentTimeMillis(),
            ),
        )
    }
}

@Preview(showBackground = true, name = "Upsert Transaction - Loading")
@Composable
private fun UpsertTransactionLoadingPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = UpsertTransactionUiState(
                isLoading = true,
                amount = "50000",
            ),
        )
    }
}

@Preview(showBackground = true, name = "Upsert Transaction - Error")
@Composable
private fun UpsertTransactionErrorPreview() {
    ArtaMiuixTheme {
        Content(
            uiState = UpsertTransactionUiState(
                amountError = "Jumlah tidak valid",
                categoryError = "Kategori wajib dipilih",
            ),
        )
    }
}

package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import android.app.DatePickerDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpsertTransactionScreen(
    transactionId: Int = 0,
    vm: UpsertTransactionVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(transactionId) {
        vm.setTransactionId(transactionId)
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is UpsertTransactionEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                UpsertTransactionEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onWalletIdChanged = vm::onWalletIdChanged,
        onAmountChanged = vm::onAmountChanged,
        onDescriptionChanged = vm::onDescriptionChanged,
        onCategoryIdChanged = vm::onCategoryIdChanged,
        onDateChanged = vm::onDateChanged,
        onClickSelectWallet = { backStack.add(WalletSelectRoute) },
        onClickSelectCategory = { backStack.add(CategorySelectRoute(categoryId = null)) },
        onClickSave = vm::submit,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState,
    uiState: UpsertTransactionUiState = UpsertTransactionUiState(),
    onWalletIdChanged: (String) -> Unit = {},
    onAmountChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onCategoryIdChanged: (String) -> Unit = {},
    onDateChanged: (String) -> Unit = {},
    onClickSelectWallet: () -> Unit = {},
    onClickSelectCategory: () -> Unit = {},
    onClickSave: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val selectedDate = remember(uiState.date) {
        runCatching { ZonedDateTime.parse(uiState.date).toLocalDate() }
            .getOrNull()
            ?: runCatching { LocalDate.parse(uiState.date.take(10)) }.getOrNull()
            ?: LocalDate.now()
    }
    val datePicker = remember(uiState.date) {
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
                title = {
                    Text(if (uiState.isUpdate) "Ubah Transaksi" else "Buat Transaksi")
                },
                navigationIcon = {
                    TextButton(onClick = onClickBack) {
                        Text("Batal")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    enabled = !uiState.isLoading,
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(if (uiState.isUpdate) "Simpan Perubahan" else "Simpan")
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = if (uiState.isUpdate) "Perbarui data transaksi." else "Isi data transaksi baru.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SnackbarHost(hostState = snackbarHostState)

            TextField(
                value = uiState.walletId,
                onValueChange = onWalletIdChanged,
                label = { Text("Wallet ID") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.walletIdError != null,
                supportingText = when {
                    uiState.walletIdError != null -> {
                        { Text(uiState.walletIdError) }
                    }

                    else -> null
                },
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Wallet", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = uiState.selectedWalletName.ifBlank { if (uiState.walletId.isBlank()) "Belum dipilih" else "Wallet ID: ${uiState.walletId}" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onClickSelectWallet, enabled = !uiState.isLoading) {
                    Text("Pilih wallet")
                }
            }

            TextField(
                value = uiState.amount,
                onValueChange = onAmountChanged,
                label = { Text("Jumlah") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.amountError != null,
                supportingText = when {
                    uiState.amountError != null -> {
                        { Text(uiState.amountError) }
                    }

                    else -> null
                },
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            TextField(
                value = uiState.categoryId,
                onValueChange = onCategoryIdChanged,
                label = { Text("Kategori ID") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.categoryError != null,
                supportingText = when {
                    uiState.categoryError != null -> {
                        { Text(uiState.categoryError) }
                    }

                    else -> null
                },
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Kategori", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = uiState.selectedCategoryName.ifBlank { if (uiState.categoryId.isBlank()) "Belum dipilih" else "Kategori #${uiState.categoryId}" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onClickSelectCategory, enabled = !uiState.isLoading) {
                    Text("Pilih kategori")
                }
            }

            TextField(
                value = uiState.date,
                onValueChange = {},
                label = { Text("Tanggal (ISO 8601)") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.dateError != null,
                supportingText = when {
                    uiState.dateError != null -> {
                        { Text(uiState.dateError) }
                    }

                    else -> {
                        { Text("Contoh: 2026-05-16T10:30:00+07:00") }
                    }
                },
                enabled = !uiState.isLoading,
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    TextButton(onClick = { datePicker.show() }, enabled = !uiState.isLoading) {
                        Text("Pilih")
                    }
                },
            )

            TextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = false,
                minLines = 4,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Create Transaction")
@Composable
private fun CreateTransactionPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            uiState = UpsertTransactionUiState(
                walletId = "12",
                selectedWalletName = "Tabungan Uang",
                categoryId = "3",
                selectedCategoryName = "Makanan",
                amount = "50000",
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Update Transaction")
@Composable
private fun UpdateTransactionPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            uiState = UpsertTransactionUiState(
                transactionId = 10,
                isUpdate = true,
                walletId = "12",
                selectedWalletName = "Wallet Utama",
                categoryId = "3",
                selectedCategoryName = "Makanan",
                amount = "50000",
                description = "Contoh transaksi",
                date = "2026-05-16T10:30:00+07:00",
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Upsert Transaction - Loading")
@Composable
private fun UpsertTransactionLoadingPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            uiState = UpsertTransactionUiState(
                isLoading = true,
                walletId = "12",
                selectedWalletName = "Wallet Utama",
                categoryId = "3",
                selectedCategoryName = "Makanan",
                amount = "50000",
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Upsert Transaction - Error")
@Composable
private fun UpsertTransactionErrorPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            uiState = UpsertTransactionUiState(
                walletIdError = "Wallet wajib dipilih",
                amountError = "Jumlah tidak valid",
                categoryError = "Kategori wajib dipilih",
                dateError = "Tanggal wajib diisi",
            ),
        )
    }
}
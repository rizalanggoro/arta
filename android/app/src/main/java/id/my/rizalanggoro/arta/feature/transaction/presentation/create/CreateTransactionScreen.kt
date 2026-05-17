package id.my.rizalanggoro.arta.feature.transaction.presentation.create

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
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
import android.app.DatePickerDialog
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.Routes.WalletSelectRoute

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateTransactionScreen(
    walletId: Int? = null,
    vm: CreateTransactionVM = viewModel(factory = CreateTransactionVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(walletId) {
        if (walletId != null && uiState.walletId.isBlank()) vm.onWalletIdChanged(walletId.toString())
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is CreateTransactionEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                CreateTransactionEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        walletId = uiState.walletId,
        selectedWalletName = uiState.selectedWalletName,
        amount = uiState.amount,
        categoryId = uiState.categoryId,
        selectedCategoryName = uiState.selectedCategoryName,
        description = uiState.description,
        date = uiState.date,
        walletIdError = uiState.walletIdError,
        amountError = uiState.amountError,
        categoryError = uiState.categoryError,
        dateError = uiState.dateError,
        isLoading = uiState.isLoading,
        onAmountChanged = vm::onAmountChanged,
        onDescriptionChanged = vm::onDescriptionChanged,
        onDateChanged = vm::onDateChanged,
        onClickSelectWallet = { backStack.add(WalletSelectRoute) },
        onClickSelectCategory = { backStack.add(CategorySelectRoute) },
        onClickSave = vm::createTransaction,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    snackbarHostState: SnackbarHostState,
    walletId: String = "",
    selectedWalletName: String = "",
    amount: String = "",
    categoryId: String = "",
    selectedCategoryName: String = "",
    description: String = "",
    date: String = "",
    walletIdError: String? = null,
    amountError: String? = null,
    categoryError: String? = null,
    dateError: String? = null,
    isLoading: Boolean = false,
    onAmountChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onDateChanged: (String) -> Unit = {},
    onClickSelectWallet: () -> Unit = {},
    onClickSelectCategory: () -> Unit = {},
    onClickSave: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Transaksi") },
                navigationIcon = {
                    TextButton(onClick = onClickBack) { Text("Batal") }
                },
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onClickSave, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) else Text("Simpan Transaksi")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Masukkan detail transaksi.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            SnackbarHost(hostState = snackbarHostState)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(text = "Wallet aktif", style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = selectedWalletName.ifBlank { if (walletId.isBlank()) "Belum dipilih" else "Wallet ID: $walletId" },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(onClick = onClickSelectWallet, enabled = !isLoading) {
                            Text("Pilih wallet")
                        }
                        if (walletIdError != null) {
                            Text(walletIdError, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            OutlinedTextField(value = amount, onValueChange = onAmountChanged, label = { Text("Jumlah") }, modifier = Modifier.fillMaxWidth(), isError = amountError != null, supportingText = when {
                amountError != null -> {
                    { Text(amountError) }
                }
                else -> null
            }, enabled = !isLoading, singleLine = true)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Kategori", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = selectedCategoryName.ifBlank { "Belum dipilih" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onClickSelectCategory, enabled = !isLoading) {
                    Text("Pilih kategori")
                }
                if (categoryError != null) {
                    Text(text = categoryError, color = MaterialTheme.colorScheme.error)
                } else if (categoryId.isNotBlank()) {
                    Text(text = "ID kategori: $categoryId", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

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
                        val iso = zoned.format(dateFormatter)
                        onDateChanged(iso)
                    },
                    selectedDate.year,
                    selectedDate.monthValue - 1,
                    selectedDate.dayOfMonth,
                )
            }

            OutlinedTextField(
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
                trailingIcon = { TextButton(onClick = { datePicker.show() }, enabled = !isLoading) { Text("Pilih") } },
            )

            OutlinedTextField(value = description, onValueChange = onDescriptionChanged, label = { Text("Deskripsi (opsional)") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading, singleLine = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTransactionPreview() {
    ArtaTheme { Content(snackbarHostState = remember { SnackbarHostState() }) }
}

@Preview(showBackground = true, name = "Create Transaction - Loading")
@Composable
private fun CreateTransactionLoadingPreview() {
    ArtaTheme { Content(snackbarHostState = remember { SnackbarHostState() }, isLoading = true) }
}

@Preview(showBackground = true, name = "Create Transaction - Error")
@Composable
private fun CreateTransactionErrorPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            walletId = "12",
            selectedWalletName = "Tabungan Uang",
            walletIdError = "Wallet aktif belum dipilih",
            amountError = "Jumlah tidak valid",
            categoryError = "Kategori wajib dipilih",
            dateError = "Tanggal wajib diisi",
        )
    }
}

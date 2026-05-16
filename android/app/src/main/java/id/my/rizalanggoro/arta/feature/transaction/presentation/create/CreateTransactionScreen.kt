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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.domain.Category
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
        type = uiState.type,
        amount = uiState.amount,
        categoryId = uiState.categoryId,
        selectedCategoryName = uiState.selectedCategoryName,
        description = uiState.description,
        date = uiState.date,
        walletIdError = uiState.walletIdError,
        typeError = uiState.typeError,
        amountError = uiState.amountError,
        dateError = uiState.dateError,
        isLoading = uiState.isLoading,
        onWalletIdChanged = vm::onWalletIdChanged,
        onTypeChanged = vm::onTypeChanged,
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
    type: String = "",
    amount: String = "",
    categoryId: String = "",
    selectedCategoryName: String = "",
    description: String = "",
    date: String = "",
    walletIdError: String? = null,
    typeError: String? = null,
    amountError: String? = null,
    dateError: String? = null,
    isLoading: Boolean = false,
    onWalletIdChanged: (String) -> Unit = {},
    onTypeChanged: (String) -> Unit = {},
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
                Text(text = "Wallet", style = MaterialTheme.typography.labelLarge)
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

            // Type selector as chips
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val types = listOf("income", "expense")
                types.forEach { t ->
                    FilterChip(selected = type == t, onClick = { onTypeChanged(t) }, label = { Text(t.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) })
                }
            }

            OutlinedTextField(value = amount, onValueChange = onAmountChanged, label = { Text("Jumlah") }, modifier = Modifier.fillMaxWidth(), isError = amountError != null, supportingText = { if (amountError != null) Text(amountError) }, enabled = !isLoading, singleLine = true)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Kategori", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = selectedCategoryName.ifBlank { "Belum dipilih" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onClickSelectCategory, enabled = !isLoading) {
                    Text("Pilih kategori")
                }
                if (categoryId.isNotBlank()) {
                    Text(
                        text = "ID kategori: $categoryId",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Date picker: open native DatePickerDialog and format to ISO offset
            val context = LocalContext.current
            val dateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val now = remember { ZonedDateTime.now() }
            val calendarDefault = now.toLocalDate()
            val datePicker = remember {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val localDate = LocalDate.of(year, month + 1, dayOfMonth)
                        val zoned = localDate.atStartOfDay(ZoneId.systemDefault())
                        val iso = zoned.format(dateFormatter)
                        onDateChanged(iso)
                    },
                    calendarDefault.year,
                    calendarDefault.monthValue - 1,
                    calendarDefault.dayOfMonth
                )
            }

            OutlinedTextField(value = date, onValueChange = {}, label = { Text("Tanggal (ISO 8601)") }, modifier = Modifier.fillMaxWidth(), isError = dateError != null, supportingText = { if (dateError != null) Text(dateError) else Text("Contoh: 2026-05-16T10:30:00+07:00") }, enabled = !isLoading, singleLine = true, readOnly = true, trailingIcon = { TextButton(onClick = { datePicker.show() }) { Text("Pilih") } })

            OutlinedTextField(value = description, onValueChange = onDescriptionChanged, label = { Text("Deskripsi (opsional)") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading, singleLine = false)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTransactionPreview() {
    ArtaTheme { Content(snackbarHostState = remember { SnackbarHostState() }) }
}

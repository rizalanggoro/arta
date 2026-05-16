package id.my.rizalanggoro.arta.feature.transaction.presentation.update

import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionUiState
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionUiState as UiState
import id.my.rizalanggoro.arta.feature.transaction.presentation.create.CreateTransactionVM
import id.my.rizalanggoro.arta.feature.transaction.presentation.update.UpdateTransactionVM
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTransactionScreen(
    transactionId: Int,
    vm: UpdateTransactionVM = viewModel(factory = UpdateTransactionVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(transactionId) { vm.load(transactionId) }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is UpdateTransactionEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                UpdateTransactionEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    // reuse simple content from create screen but wired to vm
    Scaffold(topBar = {
        TopAppBar(title = { Text("Ubah Transaksi") }, navigationIcon = { TextButton(onClick = { backStack.removeLastOrNull() }) { Text("Batal") } })
    }, bottomBar = {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { vm.updateTransaction(transactionId) }, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) else Text("Simpan Perubahan")
            }
        }
    }) { paddingValues ->
        val walletId = uiState.walletId
        val walletIdError = uiState.walletIdError
        val type = uiState.type
        val typeError = uiState.typeError
        val amount = uiState.amount
        val amountError = uiState.amountError
        val categoryId = uiState.categoryId
        val selectedCategoryName = uiState.selectedCategoryName
        val date = uiState.date
        val dateError = uiState.dateError
        val description = uiState.description

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SnackbarHost(hostState = snackbarHostState)
            OutlinedTextField(value = walletId, onValueChange = vm::onWalletIdChanged, label = { Text("Wallet ID") }, modifier = Modifier.fillMaxWidth(), isError = walletIdError != null, supportingText = { walletIdError?.let { Text(it) } }, singleLine = true)

            // Type chips
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val types = listOf("income", "expense")
                types.forEach { t ->
                    FilterChip(selected = type == t, onClick = { vm.onTypeChanged(t) }, label = { Text(t.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) })
                }
            }

            OutlinedTextField(value = amount, onValueChange = vm::onAmountChanged, label = { Text("Jumlah") }, modifier = Modifier.fillMaxWidth(), isError = amountError != null, supportingText = { amountError?.let { Text(it) } }, singleLine = true)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Kategori", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = selectedCategoryName.ifBlank { "Belum dipilih" },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = { backStack.add(CategorySelectRoute) }, enabled = !uiState.isLoading) {
                    Text("Pilih kategori")
                }
                if (categoryId.isNotBlank()) {
                    Text(
                        text = "ID kategori: $categoryId",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Date picker
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
                        vm.onDateChanged(iso)
                    },
                    calendarDefault.year,
                    calendarDefault.monthValue - 1,
                    calendarDefault.dayOfMonth
                )
            }

            OutlinedTextField(value = date, onValueChange = {}, label = { Text("Tanggal (ISO 8601)") }, modifier = Modifier.fillMaxWidth(), isError = dateError != null, supportingText = { if (dateError != null) Text(dateError) else Text("Contoh: 2026-05-16T10:30:00+07:00") }, singleLine = true, readOnly = true, trailingIcon = { TextButton(onClick = { datePicker.show() }) { Text("Pilih") } })

            OutlinedTextField(value = description, onValueChange = vm::onDescriptionChanged, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun UpdateTransactionPreview() {
    ArtaTheme { /* cannot preview ViewModel */ }
}

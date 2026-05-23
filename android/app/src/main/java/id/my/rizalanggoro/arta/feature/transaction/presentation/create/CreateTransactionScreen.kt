package id.my.rizalanggoro.arta.feature.transaction.presentation.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Category
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.shared.component.MyDatePickerDialog
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateTransactionScreen(
    vm: CreateTransactionVM = viewModel(factory = CreateTransactionVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is CreateTransactionEvent.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                CreateTransactionEvent.Success -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        wallet = uiState.wallet,
        category = uiState.category,
        amount = uiState.amount,
        description = uiState.description,
        date = uiState.date,
        amountError = uiState.amountError,
        categoryError = uiState.categoryError,
        dateError = uiState.dateError,
        isLoading = uiState.isLoading,
        onAmountChanged = vm::onAmountChanged,
        onDescriptionChanged = vm::onDescriptionChanged,
        onClickSelectCategory = { backStack.add(CategorySelectRoute) },
        onClickSubmit = vm::createTransaction,
        onClickBack = { backStack.removeLastOrNull() },
        onClickSelectDate = { vm.onChangeDatePickerDialog(isOpen = true) }
    )

    if (uiState.isDatePickerOpen)
        MyDatePickerDialog(
            state = datePickerState,
            onDismiss = { vm.onChangeDatePickerDialog(isOpen = false) },
            onDateSelected = {
                if (it != null)
                    vm.onChangeDate(it)
            }
        )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    wallet: DomainWallet? = null,
    category: DomainCategory? = null,
    amount: String = "",
    description: String = "",
    amountError: String? = null,
    categoryError: String? = null,
    dateError: String? = null,
    isLoading: Boolean = false,
    onAmountChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onClickSelectCategory: () -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickBack: () -> Unit = {},
    date: Long = System.currentTimeMillis(),
    onClickSelectDate: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                title = { Text("Buat Transaksi") },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextField(
                value = amount,
                onValueChange = onAmountChanged,
                label = { Text("Nominal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isError = amountError != null,
                supportingText = amountError?.let { { Text(it) } },
                enabled = !isLoading,
                singleLine = true,
            )

            ListItem(
                modifier = Modifier.padding(top = 16.dp),
                leadingContent = {
                    Icon(
                        Icons.Rounded.Wallet,
                        contentDescription = null,
                    )
                },
                headlineContent = {
                    Text(wallet?.name ?: "Dompet")
                },
                supportingContent = {
                    Text(
                        wallet?.type?.let(::walletTypeLabel)
                            ?: "Tidak ada dompet yang aktif",
                    )
                },
            )

            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Rounded.Category,
                        contentDescription = null,
                    )
                },
                headlineContent = { Text("Kategori") },
                supportingContent = {
                    when {
                        categoryError != null -> Text(
                            categoryError,
                            color = MaterialTheme.colorScheme.error
                        )

                        else -> Text((category?.name ?: "").ifBlank { "Pilih kategori" })
                    }
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.clickable(enabled = !isLoading) {
                    onClickSelectCategory()
                },
            )

            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Rounded.Today,
                        contentDescription = null,
                    )
                },
                headlineContent = { Text("Tanggal") },
                supportingContent = {
                    when {
                        dateError != null -> Text(
                            dateError,
                            color = MaterialTheme.colorScheme.error
                        )

                        else -> Text(date.toIndonesianDate())
                    }
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.clickable(enabled = !isLoading) {
                    onClickSelectDate()
                },
            )

            TextField(
                value = description,
                onValueChange = onDescriptionChanged,
                label = { Text("Catatan (opsional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                enabled = !isLoading,
                singleLine = false,
                minLines = 5,
            )

            if (isLoading) {
                LoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                )
            } else {
                Button(
                    onClick = onClickSubmit,
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

private fun walletTypeLabel(type: String): String {
    return when (type) {
        "cash_savings" -> "Tabungan Uang"
        "gold_savings" -> "Tabungan Emas"
        else -> type.replace('_', ' ')
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTransactionPreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, name = "Create Transaction - Loading")
@Composable
private fun CreateTransactionLoadingPreview() {
    ArtaTheme {
        Content(isLoading = true)
    }
}

@Preview(showBackground = true, name = "Create Transaction - Error")
@Composable
private fun CreateTransactionErrorPreview() {
    ArtaTheme {
        Content(
            wallet = DomainWallet(
                id = 12,
                userID = 10,
                name = "Tabungan Uang",
                type = "cash_savings",
            ),
            amountError = "Nominal tidak valid",
            categoryError = "Kategori tidak boleh kosong",
            dateError = "Tanggal tidak boleh kosong",
        )
    }
}
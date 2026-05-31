package id.my.rizalanggoro.arta.feature.transaction.presentation.upsert

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategorySelectRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.isValidInputNumber
import id.my.rizalanggoro.arta.core.extension.toIndonesianDate
import id.my.rizalanggoro.arta.shared.component.MyDatePickerDialog
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@OptIn(ExperimentalMaterial3Api::class)
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
                CategorySelectRoute(
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

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = {
                    Text(
                        when {
                            uiState.isUpdate -> "Ubah Transaksi"
                            else -> "Tambah Transaksi"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack, null
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
                .padding(horizontal = 16.dp),
        ) {
            ListItem(
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                leadingContent = {
                    Icon(
                        Icons.Rounded.Wallet, null
                    )
                },
                headlineContent = {
                    Text("Dompet")
                },
                supportingContent = {
                    Text(uiState.selectedWallet?.name ?: "Tidak ada dompet")
                },
                trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight, null
                    )
                })

            TextField(
                value = uiState.amount,
                onValueChange = {
                    if (it.isValidInputNumber()) onAmountChanged(it)
                },
                label = { Text("Jumlah") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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

            ListItem(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp
                        )
                    )
                    .clickable {
                        onClickSelectCategory()
                    }, colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ), leadingContent = {
                    Icon(
                        Icons.Rounded.Category, null
                    )
                }, headlineContent = {
                    Text("Kategori")
                }, supportingContent = {
                    Text(uiState.selectedCategory?.name ?: "Pilih kategori")
                }, trailingContent = {
                    Icon(
                        Icons.Rounded.ChevronRight, null
                    )
                }
            )

            ListItem(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp
                        )
                    )
                    .clickable {
                        onClickSelectDate()
                    },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                leadingContent = {
                    Icon(
                        Icons.Rounded.Today, null
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
                        Icons.Rounded.ChevronRight, null
                    )
                }
            )

            TextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Catatan (opsional)") },
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
                    LoadingIndicator()
                }

                else -> Button(
                    onClick = onClickSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Create Transaction")
@Composable
private fun CreateTransactionPreview() {
    ArtaTheme {
        Content(
            uiState = UpsertTransactionUiState(
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Upsert Transaction - Loading")
@Composable
private fun UpsertTransactionLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = UpsertTransactionUiState(
                isLoading = true,
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
            uiState = UpsertTransactionUiState(
                amountError = "Jumlah tidak valid",
                categoryError = "Kategori wajib dipilih",
            ),
        )
    }
}
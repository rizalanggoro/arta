package id.my.rizalanggoro.arta.feature.wallet.presentation.upsert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.constant.walletTypes
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UpsertWalletScreen(
    walletId: Int = 0,
    vm: UpsertWalletVM = viewModel(factory = UpsertWalletVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(walletId) {
        vm.loadWallet(walletId)
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is UpsertWalletEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                UpsertWalletEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        title = if (walletId == 0) "Tambah Dompet" else "Ubah Dompet",
        name = uiState.name,
        type = uiState.type,
        nameError = uiState.nameError,
        typeError = uiState.typeError,
        errorMessage = uiState.errorMessage,
        isLoading = uiState.isLoading,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::submit,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState,
    title: String,
    name: String = "",
    type: String = "cash_savings",
    nameError: String? = null,
    typeError: String? = null,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (errorMessage != null) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            TextField(
                value = name,
                onValueChange = onChangeName,
                label = { Text("Nama wallet") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = when {
                    nameError != null -> { { Text(nameError) } }
                    else -> null
                },
                enabled = !isLoading,
                singleLine = true,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tipe wallet", style = MaterialTheme.typography.labelMedium)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    walletTypes.mapIndexed { index, item ->
                        SegmentedButton(
                            selected = type == item.value,
                            onClick = { onChangeType(item.value) },
                            shape = SegmentedButtonDefaults.itemShape(
                                count = walletTypes.size,
                                index = index,
                            ),
                            enabled = !isLoading,
                        ) {
                            Text(item.name)
                        }
                    }
                }
                if (typeError != null) {
                    Text(text = typeError, color = MaterialTheme.colorScheme.error)
                }
            }

            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }
                else -> Button(
                    onClick = onClickSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                ) {
                    Text("Simpan")
                }
            }

            TextButton(onClick = onClickBack) {
                Text("Batal")
            }
        }
    }
}

@Preview(showBackground = true, name = "Wallet Upsert")
@Composable
private fun UpsertWalletPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            title = "Tambah Dompet",
            type = "cash_savings",
        )
    }
}

@Preview(showBackground = true, name = "Wallet Upsert - Loading")
@Composable
private fun UpsertWalletLoadingPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            title = "Ubah Dompet",
            isLoading = true,
            type = "gold_savings",
        )
    }
}

@Preview(showBackground = true, name = "Wallet Upsert - Error")
@Composable
private fun UpsertWalletErrorPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            title = "Tambah Dompet",
            nameError = "Nama wallet wajib diisi",
            typeError = "Tipe wallet wajib diisi",
            errorMessage = "Gagal memuat wallet",
        )
    }
}

package id.my.rizalanggoro.arta.feature.wallet.presentation.upsert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.constant.walletTypes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UpsertWalletScreen(
    walletId: Int = 0,
    vm: UpsertWalletVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(walletId) {
        vm.setWalletId(walletId)
    }

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.WalletChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        title = if (walletId == 0) "Tambah Dompet" else "Ubah Dompet",
        uiState = uiState,
        onChangeName = vm::onNameChanged,
        onChangeType = vm::onTypeChanged,
        onClickSubmit = vm::submit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    title: String = "Tambah Dompet",
    uiState: UpsertWalletUiState = UpsertWalletUiState(),
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)

        TextField(
            value = uiState.name,
            onValueChange = onChangeName,
            label = { Text("Nama wallet") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.nameError != null,
            supportingText = when {
                uiState.nameError != null -> {
                    { Text(uiState.nameError) }
                }

                else -> null
            },
            enabled = !uiState.isLoading,
            singleLine = true,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tipe dompet", style = MaterialTheme.typography.labelMedium)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                walletTypes.mapIndexed { index, item ->
                    SegmentedButton(
                        selected = uiState.type == item.value,
                        onClick = { onChangeType(item.value) },
                        shape = SegmentedButtonDefaults.itemShape(
                            count = walletTypes.size,
                            index = index,
                        ),
                        enabled = !uiState.isLoading,
                    ) {
                        Text(item.name)
                    }
                }
            }
        }

        when {
            uiState.isLoading -> Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }

            else -> Button(
                onClick = onClickSubmit,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Simpan")
            }
        }
    }
}

@Preview(showBackground = true, name = "Wallet Upsert")
@Composable
private fun UpsertWalletPreview() {
    ArtaTheme {
        Content(
            title = "Tambah Dompet",
            uiState = UpsertWalletUiState(
                type = "gold_savings",
            )
        )
    }
}

@Preview(showBackground = true, name = "Wallet Upsert - Loading")
@Composable
private fun UpsertWalletLoadingPreview() {
    ArtaTheme {
        Content(
            title = "Ubah Dompet",
            uiState = UpsertWalletUiState(
                isLoading = true,
                type = "gold_savings",
            )
        )
    }
}

@Preview(showBackground = true, name = "Wallet Upsert - Error")
@Composable
private fun UpsertWalletErrorPreview() {
    ArtaTheme {
        Content(
            title = "Tambah Dompet",
            uiState = UpsertWalletUiState(
                nameError = "Nama wallet wajib diisi",
                errorMessage = "Gagal memuat wallet",
            )
        )
    }
}

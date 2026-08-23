package id.my.rizalanggoro.arta.feature.wallet.presentation.upsert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.constant.walletTypes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.LocalBottomSheetTitle
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun UpsertWalletScreen(
    walletId: Int = 0,
    vm: UpsertWalletVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val title = if (walletId == 0) "Tambah Dompet" else "Ubah Dompet"
    val bottomSheetTitle = LocalBottomSheetTitle.current

    LaunchedEffect(walletId) {
        vm.setWalletId(walletId)
    }

    LaunchedEffect(title) {
        bottomSheetTitle?.value = title
    }

    DisposableEffect(Unit) {
        onDispose { bottomSheetTitle?.value = null }
    }

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.WalletChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        uiState = uiState,
        onChangeName = vm::onNameChanged,
        onChangeType = vm::onTypeChanged,
        onClickSubmit = vm::submit,
    )
}

private val ErrorColor = Color(0xFFE53935)

@Composable
private fun Content(
    uiState: UpsertWalletUiState = UpsertWalletUiState(),
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextField(
                value = uiState.name,
                onValueChange = onChangeName,
                label = "Nama wallet",
                useLabelAsPlaceholder = true,
                enabled = !uiState.isLoading,
                colors = if (uiState.nameError != null) {
                    TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                } else {
                    TextFieldDefaults.textFieldColors()
                },
            )
            uiState.nameError?.let { error ->
                Text(error, fontSize = 13.sp, color = ErrorColor)
            }
        }

        SmallTitle(
            "Tipe dompet",
            insideMargin = PaddingValues(top = 8.dp),
        )

        TabRowWithContour(
            tabs = walletTypes.map { it.name },
            selectedTabIndex = walletTypes
                .indexOfFirst { it.value == uiState.type }
                .coerceAtLeast(0),
            onTabSelected = { index -> onChangeType(walletTypes[index].value) },
        )

        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
                    .height(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                InfiniteProgressIndicator()
            }

            else -> Button(
                onClick = onClickSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text("Simpan")
            }
        }
    }
}

@Preview(showBackground = true, name = "Wallet Upsert")
@Composable
private fun UpsertWalletPreview() {
    MiuixTheme {
        Content(
            uiState = UpsertWalletUiState(
                type = "gold_savings",
            )
        )
    }
}

@Preview(showBackground = true, name = "Wallet Upsert - Loading")
@Composable
private fun UpsertWalletLoadingPreview() {
    MiuixTheme {
        Content(
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
    MiuixTheme {
        Content(
            uiState = UpsertWalletUiState(
                nameError = "Nama wallet wajib diisi",
                errorMessage = "Gagal memuat wallet",
            )
        )
    }
}

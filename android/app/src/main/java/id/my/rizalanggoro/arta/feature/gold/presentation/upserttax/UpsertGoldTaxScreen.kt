package id.my.rizalanggoro.arta.feature.gold.presentation.upserttax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.extension.isValidInputNumber
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.shared.component.LocalBottomSheetTitle
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun UpsertGoldTaxScreen(
    taxPreferenceId: Int,
    vm: UpsertGoldTaxVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val title = if (uiState.isUpdate) "Ubah Konfigurasi Pajak" else "Tambah Konfigurasi Pajak"
    val bottomSheetTitle = LocalBottomSheetTitle.current

    LaunchedEffect(taxPreferenceId) {
        vm.setTaxPreferenceId(taxPreferenceId)
    }

    LaunchedEffect(title) {
        bottomSheetTitle?.value = title
    }

    DisposableEffect(Unit) {
        onDispose { bottomSheetTitle?.value = null }
    }

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.GoldTaxChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        isLoading = uiState.isLoading,
        carat = uiState.carat,
        caratError = uiState.caratError,
        taxRate = uiState.taxRate,
        taxRateError = uiState.taxRateError,
        errorMessage = uiState.errorMessage,
        onChangeCarat = vm::onCaratChanged,
        onChangeTaxRate = vm::onTaxRateChanged,
        onClickSubmit = vm::onSubmitClicked,
    )
}

private val ErrorColor = Color(0xFFE53935)

@Composable
private fun Content(
    isLoading: Boolean = false,
    carat: String = "",
    caratError: String? = null,
    taxRate: String = "",
    taxRateError: String? = null,
    errorMessage: String? = null,
    onChangeCarat: (String) -> Unit = {},
    onChangeTaxRate: (String) -> Unit = {},
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
                value = carat,
                onValueChange = {
                    if (it.isValidInputNumber())
                        onChangeCarat(it)
                },
                label = "Karat",
                useLabelAsPlaceholder = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = !isLoading,
                colors = if (caratError != null) {
                    TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                } else {
                    TextFieldDefaults.textFieldColors()
                },
            )
            caratError?.let { error ->
                Text(error, fontSize = 13.sp, color = ErrorColor)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TextField(
                value = taxRate,
                onValueChange = {
                    if (it.isValidInputNumber())
                        onChangeTaxRate(it)
                },
                label = "Rasio pajak (%)",
                useLabelAsPlaceholder = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                enabled = !isLoading,
                colors = if (taxRateError != null) {
                    TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                } else {
                    TextFieldDefaults.textFieldColors()
                },
            )
            taxRateError?.let { error ->
                Text(error, fontSize = 13.sp, color = ErrorColor)
            }
        }

        errorMessage?.let { error ->
            Text(error, fontSize = 13.sp, color = ErrorColor)
        }

        when {
            isLoading -> Box(
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

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MiuixTheme { Content() }
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    MiuixTheme { Content(isLoading = true) }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    MiuixTheme {
        Content(
            caratError = "Karat wajib diisi",
            taxRateError = "Rasio pajak wajib diisi",
        )
    }
}

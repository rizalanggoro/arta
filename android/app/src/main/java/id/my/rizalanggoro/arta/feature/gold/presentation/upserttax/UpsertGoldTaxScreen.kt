package id.my.rizalanggoro.arta.feature.gold.presentation.upserttax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Button
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.extension.isValidInputNumber
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun UpsertGoldTaxScreen(
    taxPreferenceId: Int,
    vm: UpsertGoldTaxVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(taxPreferenceId) {
        vm.setTaxPreferenceId(taxPreferenceId)
    }

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.GoldTaxChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        isUpdate = uiState.isUpdate,
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

@Composable
private fun Content(
    isUpdate: Boolean = false,
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
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            when {
                isUpdate -> "Ubah Konfigurasi Pajak"
                else -> "Tambah Konfigurasi Pajak"
            },
            style = MaterialTheme.typography.titleMedium,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            TextField(
                value = carat,
                onValueChange = {
                    if (it.isValidInputNumber())
                        onChangeCarat(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                supportingText = when {
                    caratError != null -> {
                        { Text(caratError) }
                    }

                    else -> null
                },
                isError = caratError != null,
                label = { Text("Karat") },
                enabled = !isLoading,
                singleLine = true,
            )
            TextField(
                value = taxRate,
                onValueChange = {
                    if (it.isValidInputNumber())
                        onChangeTaxRate(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                supportingText = when {
                    taxRateError != null -> {
                        { Text(taxRateError) }
                    }

                    else -> null
                },
                isError = taxRateError != null,
                label = { Text("Rasio pajak (%)") },
                enabled = !isLoading,
                singleLine = true,
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        when {
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }

            else -> Button(
                onClick = onClickSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            ) {
                Text("Simpan")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    ArtaTheme { Content() }
}

@Composable
@Preview(showBackground = true)
private fun LoadingPreview() {
    ArtaTheme { Content(isLoading = true) }
}
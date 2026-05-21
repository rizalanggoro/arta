package id.my.rizalanggoro.arta.feature.gold.presentation.updatetax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateGoldTaxScreen(
    taxPreferenceId: Int,
    vm: UpdateGoldTaxVM = viewModel(factory = UpdateGoldTaxVM.Factory(taxPreferenceId)),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(taxPreferenceId) {
        vm.load()
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is UpdateGoldTaxEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                UpdateGoldTaxEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        carat = uiState.carat,
        taxRate = uiState.taxRate,
        caratError = uiState.caratError,
        taxRateError = uiState.taxRateError,
        isLoading = uiState.isLoading,
        isSaving = uiState.isSaving,
        errorMessage = uiState.errorMessage,
        onCaratChanged = vm::onCaratChanged,
        onTaxRateChanged = vm::onTaxRateChanged,
        onSave = vm::updateTaxPreference,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState,
    carat: String = "",
    taxRate: String = "",
    caratError: String? = null,
    taxRateError: String? = null,
    isLoading: Boolean = false,
    isSaving: Boolean = false,
    errorMessage: String? = null,
    onCaratChanged: (String) -> Unit = {},
    onTaxRateChanged: (String) -> Unit = {},
    onSave: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Ubah pajak emas") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { paddingValues ->
        when {
            isLoading -> Column(
                modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Memuat preferensi pajak...")
            }

            errorMessage != null -> Column(
                modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(text = errorMessage)
            }

            else -> Column(
                modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextField(
                    value = carat,
                    onValueChange = onCaratChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Karat") },
                    isError = caratError != null,
                    supportingText = { if (caratError != null) Text(caratError) },
                )
                TextField(
                    value = taxRate,
                    onValueChange = onTaxRateChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rasio pajak (%)") },
                    isError = taxRateError != null,
                    supportingText = { if (taxRateError != null) Text(taxRateError) },
                )
                Button(
                    onClick = onSave,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (isSaving) "Menyimpan..." else "Simpan")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewUpdateGoldTaxScreen() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            carat = "24",
            taxRate = "5",
        )
    }
}
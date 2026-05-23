package id.my.rizalanggoro.arta.feature.wallet.presentation.createfirst

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.HomeRoute
import id.my.rizalanggoro.arta.core.constant.walletTypes
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFirstWalletScreen(vm: CreateFirstWalletVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<CreateFirstWalletUiState.Event.ShowMessage>()
            .collect { snackbarHostState.showSnackbar(it.message) }
    }

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<CreateFirstWalletUiState.Event.CreateSucceeded>()
            .collect {
                backStack.clear()
                backStack.add(HomeRoute)
            }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::create,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    uiState: CreateFirstWalletUiState = CreateFirstWalletUiState(),
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buat Dompet Pertama") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                value = uiState.name,
                onValueChange = onChangeName,
                label = { Text("Nama dompet") },
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

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Tipe wallet", style = MaterialTheme.typography.labelMedium)
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
                if (uiState.typeError != null) {
                    Text(
                        text = uiState.typeError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
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

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateFirstWalletPreview() {
    ArtaTheme {
        Content(
            uiState = CreateFirstWalletUiState(
                type = "cash_savings"
            )
        )
    }
}

@Preview(showBackground = true, group = "Wallet")
@Composable
private fun CreateFirstWalletLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = CreateFirstWalletUiState(
                type = "cash_savings",
                isLoading = true
            )
        )
    }
}
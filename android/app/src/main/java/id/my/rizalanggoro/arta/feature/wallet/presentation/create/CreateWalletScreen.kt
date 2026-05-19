package id.my.rizalanggoro.arta.feature.wallet.presentation.create

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.constant.walletTypes
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(vm: CreateWalletVM = viewModel(factory = CreateWalletVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = id.my.rizalanggoro.arta.core.LocalBackStack.current

    LaunchedEffect(Unit) {
        vm.event
            .filterIsInstance<CreateWalletEvent.Succeeded>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        isLoading = uiState.isLoading,
        name = uiState.name,
        type = uiState.type,
        nameError = uiState.nameError,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::create,
        onClickBack = { backStack.removeLastOrNull() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    isLoading: Boolean = false,
    name: String = "",
    type: String = "",
    nameError: String? = null,
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = { Text("Tambah Dompet") })
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TextField(
                value = name,
                onValueChange = onChangeName,
                label = { Text("Nama dompet") },
                modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
                isError = nameError != null,
                supportingText = when {
                    nameError != null -> {
                        { Text(nameError) }
                    }

                    else -> null
                },
                enabled = !isLoading,
                singleLine = true,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
					.padding(top = 16.dp)
					.padding(horizontal = 16.dp)
            ) {
                Text("Tipe dompet", style = MaterialTheme.typography.labelMedium)
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    walletTypes.mapIndexed { index, item ->
                        SegmentedButton(
                            selected = type == item.value,
                            onClick = { onChangeType(item.value) },
                            shape = SegmentedButtonDefaults.itemShape(
                                count = walletTypes.size,
                                index = index
                            ),
                            enabled = !isLoading,
                        ) {
                            Text(item.name)
                        }
                    }
                }
            }

            when (isLoading) {
                true -> Box(
                    modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }

                false -> Button(
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

@Preview(showBackground = true, group = "Wallet", name = "Create Wallet")
@Composable
private fun CreateWalletPreview() {
    ArtaTheme {
        Content(
            type = "cash_savings"
        )
    }
}

@Preview(showBackground = true, group = "Wallet", name = "Create Wallet - Loading")
@Composable
private fun CreateWalletLoadingPreview() {
    ArtaTheme {
        Content(
            isLoading = true,
            type = "gold_savings"
        )
    }
}

@Preview(showBackground = true, group = "Wallet", name = "Create Wallet - With Error")
@Composable
private fun CreateWalletErrorPreview() {
    ArtaTheme {
        Content(
            type = "cash_savings",
            nameError = "Nama dompet tidak boleh kosong",
        )
    }
}
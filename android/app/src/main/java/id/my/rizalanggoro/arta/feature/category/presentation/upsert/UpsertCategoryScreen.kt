package id.my.rizalanggoro.arta.feature.category.presentation.upsert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun UpsertCategoryScreen(
    categoryId: Int,
    vm: UpsertCategoryVM = viewModel(factory = UpsertCategoryVM.Factory(categoryId)),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(categoryId) {
        vm.loadCategory()
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                UpsertCategoryEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        isUpdate = uiState.isUpdate,
        isLoading = uiState.isLoading,
        name = uiState.name,
        nameError = uiState.nameError,
        type = uiState.type,
        errorMessage = uiState.errorMessage,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::submit,
    )
}

@Composable
private fun Content(
    isUpdate: Boolean = false,
    isLoading: Boolean = false,
    name: String = "",
    nameError: String? = null,
    type: String = "expense",
    errorMessage: String? = null,
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = when {
                isUpdate -> "Ubah Kategori"
                else -> "Buat Kategori"
            },
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = when {
                isUpdate -> "Perbarui nama dan tipe kategori."
                else -> "Isi nama dan tipe kategori untuk transaksi."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )

        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                value = name,
                onValueChange = onChangeName,
                label = { Text("Nama kategori") },
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
                Text(
                    text = "Tipe kategori",
                    style = MaterialTheme.typography.titleMedium,
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    categoryTypes.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = type == option.value,
                            onClick = { onChangeType(option.value) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = categoryTypes.size,
                            ),
                            enabled = !isLoading,
                        ) {
                            Text(option.name)
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
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
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Category Upsert - Create")
@Composable
private fun CreatePreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, name = "Category Upsert - Update", showSystemUi = false)
@Composable
private fun UpdatePreview() {
    ArtaTheme {
        Content(
            isUpdate = true,
            name = "Makanan",
            type = "expense",
        )
    }
}

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
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun UpsertCategoryScreen(
    vm: UpsertCategoryVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.CategoryChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        uiState = uiState,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::submit,
    )
}

@Composable
private fun Content(
    uiState: UpsertCategoryUiState = UpsertCategoryUiState(),
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = when {
                uiState.isUpdate -> "Ubah Kategori"
                else -> "Buat Kategori"
            },
            style = MaterialTheme.typography.titleLarge,
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TextField(
                value = uiState.name,
                onValueChange = onChangeName,
                label = { Text("Nama kategori") },
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
                Text(
                    text = "Tipe kategori",
                    style = MaterialTheme.typography.titleMedium,
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    categoryTypes.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = uiState.type == option.value,
                            onClick = { onChangeType(option.value) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = categoryTypes.size,
                            ),
                            enabled = !uiState.isLoading,
                        ) {
                            Text(option.name)
                        }
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
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
}

@Preview(showBackground = true)
@Composable
private fun CreatePreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdatePreview() {
    ArtaTheme {
        Content(
            uiState = UpsertCategoryUiState(
                isUpdate = true,
                name = "Makanan",
                type = "expense",
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdateLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = UpsertCategoryUiState(
                isUpdate = true,
                isLoading = true,
                name = "Makanan",
                type = "expense",
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdateErrorPreview() {
    ArtaTheme {
        Content(
            uiState = UpsertCategoryUiState(
                isUpdate = true,
                name = "Makanan",
                type = "expense",
                nameError = "Nama kategori tidak boleh kosong",
            )
        )
    }
}

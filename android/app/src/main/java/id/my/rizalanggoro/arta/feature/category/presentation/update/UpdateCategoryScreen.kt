package id.my.rizalanggoro.arta.feature.category.presentation.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun UpdateCategoryScreen(
    categoryId: Int,
    vm: UpdateCategoryVM = viewModel(factory = UpdateCategoryVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(categoryId) {
        vm.loadCategory(categoryId)
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is UpdateCategoryEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                UpdateCategoryEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        name = uiState.name,
        type = uiState.type,
        nameError = uiState.nameError,
        errorMessage = uiState.errorMessage,
        isLoading = uiState.isLoading,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::updateCategory,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
private fun Content(
    snackbarHostState: SnackbarHostState,
    name: String = "",
    type: String = "expense",
    nameError: String? = null,
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    val typeOptions = listOf(
        CategoryTypeOption(label = "Pengeluaran", value = "expense"),
        CategoryTypeOption(label = "Pemasukan", value = "income"),
    )

    androidx.compose.material3.Scaffold(
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
                title = { Text("Ubah Kategori") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Perbarui nama kategori.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                TextField(
                    value = name,
                    onValueChange = onChangeName,
                    label = { Text("Nama kategori") },
                    modifier = Modifier.fillMaxWidth(),
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

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tipe kategori",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        typeOptions.forEach { option ->
                            FilterChip(
                                selected = type == option.value,
                                onClick = { onChangeType(option.value) },
                                label = { Text(option.label) },
                                enabled = !isLoading,
                            )
                        }
                    }
                }

                when (isLoading) {
                    true -> LoadingIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    false -> Button(
                        onClick = onClickSubmit,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}

private data class CategoryTypeOption(
    val label: String,
    val value: String,
)

@Preview(showBackground = true, name = "Update Category - Default")
@Composable
private fun UpdateCategoryDefaultPreview() {
    ArtaTheme {
        Content(snackbarHostState = remember { SnackbarHostState() })
    }
}

@Preview(showBackground = true, name = "Update Category - Loading")
@Composable
private fun UpdateCategoryLoadingPreview() {
    ArtaTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = true,
        )
    }
}
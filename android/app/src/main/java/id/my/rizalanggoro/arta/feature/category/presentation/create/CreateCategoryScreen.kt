@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package id.my.rizalanggoro.arta.feature.category.presentation.create

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateCategoryScreen(vm: CreateCategoryVM = viewModel(factory = CreateCategoryVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is CreateCategoryEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                CreateCategoryEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Content(
        snackbarHostState = snackbarHostState,
        name = uiState.name,
        type = uiState.type,
        nameError = uiState.nameError,
        isLoading = uiState.isLoading,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::createCategory,
        onClickBack = { backStack.removeLastOrNull() },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    name: String = "",
    type: String = "expense",
    nameError: String? = null,
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
                title = { Text("Buat Kategori") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
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
                    style = MaterialTheme.typography.titleMedium,
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
                true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
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

private data class CategoryTypeOption(
    val label: String,
    val value: String,
)

@Preview(showBackground = true, name = "Create Category - Default")
@Composable
private fun CreateCategoryDefaultPreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, name = "Create Category - Loading")
@Composable
private fun CreateCategoryLoadingPreview() {
    ArtaTheme {
        Content(isLoading = true)
    }
}

@Preview(showBackground = true, name = "Create Category - Error")
@Composable
private fun CreateCategoryErrorPreview() {
    ArtaTheme {
        Content(nameError = "Nama kategori wajib diisi")
    }
}
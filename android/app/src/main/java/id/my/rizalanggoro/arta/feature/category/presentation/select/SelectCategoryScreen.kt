package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SelectCategoryScreen(
    vm: SelectCategoryVM = viewModel(factory = SelectCategoryVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        vm.loadCategories()
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                SelectCategoryEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Kategori") },
                navigationIcon = {
                    TextButton(onClick = { backStack.removeLastOrNull() }) { Text("Batal") }
                },
            )
        },
    ) { paddingValues ->
        Content(
            categories = uiState.categories,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onReload = vm::loadCategories,
            onSelect = vm::selectCategory,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
        )
    }
}

@Composable
private fun Content(
    categories: List<Category> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onReload: () -> Unit = {},
    onSelect: (Category) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Pilih kategori yang akan dipakai di transaksi.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(errorMessage ?: "Gagal memuat kategori")
                        Button(onClick = onReload) { Text("Muat ulang") }
                    }
                }
            }

            categories.isEmpty() -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Belum ada kategori.")
                        Text(
                            text = "Buat kategori dulu agar bisa dipakai di transaksi.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(categories, key = { it.id }) { category ->
                        SelectCategoryCard(
                            category = category,
                            onClick = { onSelect(category) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectCategoryCard(
    category: Category,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(category.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = category.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryPreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, name = "Select Category - With Items")
@Composable
private fun SelectCategoryItemsPreview() {
    ArtaTheme {
        Content(categories = listOf(
            id.my.rizalanggoro.arta.domain.Category(id = 1, userId = null, name = "Makanan", type = "expense", icon = "🍜", color = "#F97316"),
            id.my.rizalanggoro.arta.domain.Category(id = 2, userId = 10, name = "Gaji", type = "income", icon = "💰", color = "#10B981"),
        ))
    }
}

@Preview(showBackground = true, name = "Select Category - Loading")
@Composable
private fun SelectCategoryLoadingPreview() {
    ArtaTheme { Content() }
}
@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import id.my.rizalanggoro.arta.core.Routes.CategoryCreateRoute
import id.my.rizalanggoro.arta.core.Routes.CategoryUpdateRoute
import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ListCategoryScreen(vm: ListCategoryVM = viewModel(factory = ListCategoryVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        vm.loadCategories()
    }

    Content(
        categories = uiState.categories,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        deleteTarget = uiState.deleteTarget,
        onClickCreate = { backStack.add(CategoryCreateRoute) },
        onClickEdit = { categoryId -> backStack.add(CategoryUpdateRoute(categoryId = categoryId)) },
        onClickDelete = vm::onDeleteRequested,
        onDismissDelete = vm::dismissDeleteDialog,
        onConfirmDelete = vm::confirmDeleteCategory,
        onRetry = vm::loadCategories,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    categories: List<Category> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    deleteTarget: Category? = null,
    onClickCreate: () -> Unit = {},
    onClickEdit: (Int) -> Unit = {},
    onClickDelete: (Category) -> Unit = {},
    onDismissDelete: () -> Unit = {},
    onConfirmDelete: (Category) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = { Text("Kelola Kategori") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onClickCreate) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
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
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Button(onClick = onRetry) {
                                Text("Muat ulang")
                            }
                        }
                    }
                }

                categories.isEmpty() -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = "Belum ada kategori custom.",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "Buat kategori baru untuk memudahkan pengelompokan transaksi.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            FilledTonalButton(onClick = onClickCreate) {
                                Text("Buat kategori")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(categories, key = { it.id }) { category ->
                            CategoryCard(
                                category = category,
                                onClickEdit = onClickEdit,
                                onClickDelete = onClickDelete,
                            )
                        }
                    }
                }
            }
        }
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text("Hapus kategori?") },
            text = { Text("Kategori \"${deleteTarget.name}\" akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.") },
            confirmButton = {
                Button(onClick = { onConfirmDelete(deleteTarget) }) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismissDelete) {
                    Text("Batal")
                }
            },
        )
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    onClickEdit: (Int) -> Unit,
    onClickDelete: (Category) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = category.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                AssistChip(
                    onClick = {},
                    label = { Text(if (category.userId != null) "Custom" else "Default") },
                )
            }

            if (category.userId != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilledTonalButton(
                        onClick = { onClickEdit(category.id) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Ubah")
                    }
                    OutlinedButton(
                        onClick = { onClickDelete(category) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Hapus")
                    }
                }
            } else {
                Text(
                    text = "Kategori default tidak dapat diubah atau dihapus.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Category List - Default")
@Composable
private fun ListCategoryDefaultPreview() {
    ArtaTheme {
        Content(
            categories = listOf(
                Category(
                    id = 1,
                    userId = null,
                    name = "Makanan",
                    type = "expense",
                    icon = "🍜",
                    color = "#F97316",
                ),
                Category(
                    id = 2,
                    userId = 123,
                    name = "Transport",
                    type = "expense",
                    icon = "🚌",
                    color = "#0EA5E9",
                ),
            ),
            deleteTarget = null,
        )
    }
}

@Preview(showBackground = true, name = "Category List - Loading")
@Composable
private fun ListCategoryLoadingPreview() {
    ArtaTheme {
        Content(isLoading = true)
    }
}

@Preview(showBackground = true, name = "Category List - Empty")
@Composable
private fun ListCategoryEmptyPreview() {
    ArtaTheme {
        Content()
    }
}
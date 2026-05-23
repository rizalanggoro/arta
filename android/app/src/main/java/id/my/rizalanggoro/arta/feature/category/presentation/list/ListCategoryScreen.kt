@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategoryUpsertRoute
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ListCategoryScreen(vm: ListCategoryVM = viewModel(factory = ListCategoryVM.Factory)) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        categories = uiState.categories,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        selectedType = uiState.selectedType,
        actionTarget = uiState.actionTarget,
        deleteTarget = uiState.deleteTarget,
        onClickCreate = { backStack.add(CategoryUpsertRoute()) },
        onClickType = vm::onCategoryTypeSelected,
        onClickCategory = vm::onCategoryClicked,
        onClickEdit = { categoryId -> backStack.add(CategoryUpsertRoute(categoryId = categoryId)) },
        onClickBack = { backStack.removeLastOrNull() },
        onDismissActionSheet = vm::dismissActionSheet,
        onClickDelete = vm::onDeleteRequested,
        onDismissDelete = vm::dismissDeleteDialog,
        onConfirmDelete = vm::confirmDeleteCategory,
        onRetry = vm::loadCategories,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    categories: List<DtoCategory> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    selectedType: String = "expense",
    actionTarget: DomainCategory? = null,
    deleteTarget: DomainCategory? = null,
    onClickCreate: () -> Unit = {},
    onClickType: (String) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
    onClickEdit: (Int) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickDelete: (DomainCategory) -> Unit = {},
    onDismissActionSheet: () -> Unit = {},
    onDismissDelete: () -> Unit = {},
    onConfirmDelete: (DomainCategory) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val actionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filterOptions = listOf(
        CategoryFilterOption(label = "Pengeluaran", value = "expense"),
        CategoryFilterOption(label = "Pemasukan", value = "income"),
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
                title = { Text("Kelola Kategori") },
            )
        },
        floatingActionButton = {
            if (!isLoading)
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
        ) {
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
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

                else -> Column(modifier = Modifier.fillMaxSize()) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        filterOptions.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = selectedType == option.value,
                                onClick = { onClickType(option.value) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = filterOptions.size
                                ),
                            ) {
                                Text(option.label)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                    ) {
                        item { Box(modifier = Modifier.height(8.dp)) }
                        items(categories, key = { it.data.id }) { category ->
                            ListItem(
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (category.data.type) {
                                                    "income" -> MaterialTheme.colorScheme.primaryContainer
                                                    else -> MaterialTheme.colorScheme.errorContainer
                                                }
                                            )
                                    ) {
                                        Icon(
                                            when (category.data.type) {
                                                "income" -> Icons.AutoMirrored.Rounded.CallReceived
                                                else -> Icons.AutoMirrored.Rounded.CallMade
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.align(Alignment.Center),
                                            tint = when (category.data.type) {
                                                "income" -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.error
                                            }
                                        )
                                    }
                                },
                                headlineContent = {
                                    Text(category.data.name)
                                },
                                supportingContent = when {
                                    category.data.userId != null -> null
                                    else -> {
                                        { Text("Bawaan") }
                                    }
                                },
                                modifier = Modifier.clickable(enabled = category.data.userId != null) {
                                    onClickCategory(category.data)
                                }
                            )
                        }
                        item {
                            Text(
                                "Kategori bawaan tidak dapat diubah atau dihapus",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                        item { Box(modifier = Modifier.height(88.dp)) }
                    }
                }
            }
        }
    }

    if (actionTarget != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissActionSheet,
            sheetState = actionSheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = actionTarget.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Pilih tindakan untuk kategori custom ini.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = {
                        onClickEdit(actionTarget.id)
                        onDismissActionSheet()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Ubah")
                }
                OutlinedButton(
                    onClick = {
                        onClickDelete(actionTarget)
                        onDismissActionSheet()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Hapus")
                }
                TextButton(
                    onClick = onDismissActionSheet,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Tutup")
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

private data class CategoryFilterOption(
    val label: String,
    val value: String,
)

@Preview(showBackground = true, name = "Category List - Default")
@Composable
private fun ListCategoryDefaultPreview() {
    ArtaTheme {
        Content(
            categories = listOf(
                DtoCategory(
                    data = DomainCategory(
                        createdAt = "2026-05-23T10:00:00Z",
                        id = 1,
                        name = "Makanan",
                        type = "expense",
                        updatedAt = "2026-05-23T10:00:00Z",
                        userId = null,
                    ),
                ),
                DtoCategory(
                    data = DomainCategory(
                        createdAt = "2026-05-23T10:00:00Z",
                        id = 2,
                        name = "Transport",
                        type = "expense",
                        updatedAt = "2026-05-23T10:00:00Z",
                        userId = 123,
                    ),
                ),
            ),
            selectedType = "expense",
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
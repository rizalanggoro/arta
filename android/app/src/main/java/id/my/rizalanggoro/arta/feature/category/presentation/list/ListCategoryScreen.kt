package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes.CategoryUpsertRoute
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.feature.category.presentation.list.component.CategoryActionSheet
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun ListCategoryScreen(vm: ListCategoryVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickCreate = { backStack.add(CategoryUpsertRoute()) },
        onClickType = vm::onCategoryTypeSelected,
        onClickCategory = vm::onCategoryClicked,
        onClickBack = { backStack.removeLastOrNull() },
        onClickRetry = vm::loadCategories,
    )

    if (uiState.actionTarget != null)
        CategoryActionSheet(
            category = uiState.actionTarget!!,
            onClickEdit = {
                backStack.add(
                    CategoryUpsertRoute(
                        categoryId = uiState.actionTarget!!.id
                    )
                )
            },
            onClickDelete = vm::onDeleteActionClicked,
            onDismissRequest = vm::onActionDismissed,
        )

    if (uiState.deleteTarget != null)
        ConfirmDialog(
            title = "Hapus",
            description = "Apakah Anda yakin akan menghapus kategori " +
                    "\"${uiState.deleteTarget!!.name}\"? Tindakan ini tidak dapat dipulihkan",
            onDismissRequest = vm::onDeleteDialogDismissed,
            onConfirmRequest = vm::onDeleteClicked,
            isLoading = uiState.isDeleting,
            confirmText = "Hapus"
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: ListCategoryUiState = ListCategoryUiState(),
    onClickCreate: () -> Unit = {},
    onClickType: (String) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickRetry: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            null
                        )
                    }
                },
                title = { Text("Kelola Kategori") },
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading)
                FloatingActionButton(onClick = onClickCreate) {
                    Icon(
                        Icons.Rounded.Add,
                         null
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
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }

                uiState.errorMessage != null -> ErrorPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    message = uiState.errorMessage,
                    onClickRetry = onClickRetry
                )

                uiState.incomeCategories.isEmpty() || uiState.expenseCategories.isEmpty() -> EmptyPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                else -> Column(modifier = Modifier.fillMaxSize()) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        categoryTypes.forEachIndexed { index, item ->
                            SegmentedButton(
                                selected = uiState.selectedType == item.value,
                                onClick = { onClickType(item.value) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = categoryTypes.size
                                ),
                            ) {
                                Text(item.name)
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                    ) {
                        item { Box(modifier = Modifier.height(8.dp)) }
                        items(
                            when {
                                uiState.selectedType == "income" -> uiState.incomeCategories
                                else -> uiState.expenseCategories
                            }
                        ) { category ->
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
                                supportingContent = {
                                    Text(
                                        when {
                                            category.data.userId == null -> "Bawaan"
                                            else -> "Kustom"
                                        }
                                    )
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
}

@Preview(showBackground = true, name = "Category List - Default")
@Composable
private fun ListCategoryDefaultPreview() {
    ArtaTheme {
        Content(
            uiState = ListCategoryUiState(
                incomeCategories = listOf(
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
            )
        )
    }
}

@Preview(showBackground = true, name = "Category List - Loading")
@Composable
private fun ListCategoryLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = ListCategoryUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, name = "Category List - Empty")
@Composable
private fun ListCategoryEmptyPreview() {
    ArtaTheme {
        Content()
    }
}

@Preview(showBackground = true, name = "Category List - Empty")
@Composable
private fun ListCategoryErrorPreview() {
    ArtaTheme {
        Content(
            uiState = ListCategoryUiState(
                errorMessage = "Terjadi kesalahan tak terduga"
            )
        )
    }
}
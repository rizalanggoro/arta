package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun SelectCategoryScreen(
    selectedCategoryId: Int? = null,
    vm: SelectCategoryVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.CategorySelected>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        selectedCategoryId = selectedCategoryId,
        uiState = uiState,
        onClickRetry = vm::loadCategories,
        onClickType = vm::onCategoryTypeSelected,
        onClickCategory = vm::selectCategory,
    )
}

@Composable
private fun Content(
    selectedCategoryId: Int? = null,
    uiState: SelectCategoryUiState = SelectCategoryUiState(),
    onClickRetry: () -> Unit = {},
    onClickType: (String) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
) {
    val visibleCategories = uiState.categories.filter { it.data.type == uiState.selectedType }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pilih Kategori",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (!uiState.isLoading && visibleCategories.isNotEmpty() && uiState.errorMessage.isNullOrEmpty())
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                categoryTypes.mapIndexed { index, item ->
                    SegmentedButton(
                        selected = uiState.selectedType == item.value,
                        onClick = { onClickType(item.value) },
                        shape = SegmentedButtonDefaults.itemShape(
                            count = categoryTypes.size,
                            index = index,
                        ),
                    ) {
                        Text(item.name)
                    }
                }
            }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingIndicator()
                }
            }

            uiState.errorMessage != null -> ErrorPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                message = uiState.errorMessage,
                onClickRetry = onClickRetry,
            )

            visibleCategories.isEmpty() -> EmptyPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            )

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visibleCategories) { category ->
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = {
                                RadioButton(
                                    selected = selectedCategoryId == category.data.id,
                                    onClick = { onClickCategory(category.data) },
                                )
                            },
                            headlineContent = {
                                Text(category.data.name)
                            },
                            modifier = Modifier.clickable {
                                onClickCategory(category.data)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryPreview() {
    ArtaTheme { Content() }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryItemsPreview() {
    ArtaTheme {
        Content(
            selectedCategoryId = 1,
            uiState = SelectCategoryUiState(
                categories = listOf(
                    DtoCategory(
                        data = DomainCategory(
                            createdAt = "2026-05-23T10:00:00Z",
                            id = 1,
                            name = "Makanan",
                            type = "expense",
                            updatedAt = "2026-05-23T10:00:00Z",
                            userId = null,
                        )
                    ),
                    DtoCategory(
                        data = DomainCategory(
                            createdAt = "2026-05-23T10:00:00Z",
                            id = 2,
                            name = "Gaji",
                            type = "income",
                            updatedAt = "2026-05-23T10:00:00Z",
                            userId = 10,
                        )
                    ),
                ),
                selectedType = "expense",
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryLoadingPreview() {
    ArtaTheme {
        Content(
            uiState = SelectCategoryUiState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryErrorPreview() {
    ArtaTheme {
        Content(
            uiState = SelectCategoryUiState(
                errorMessage = "Terjadi kesalahan tak terduga"
            )
        )
    }
}

package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.RadioButton
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
        onClickManageCategory = {
            backStack.apply {
                removeLastOrNull()
                add(CategoryRoute.List)
            }
        }
    )
}

@Composable
private fun Content(
    selectedCategoryId: Int? = null,
    uiState: SelectCategoryUiState = SelectCategoryUiState(),
    onClickRetry: () -> Unit = {},
    onClickType: (String) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
    onClickManageCategory: () -> Unit = {},
) {
    val visibleCategories = uiState.categories.filter { it.data.type == uiState.selectedType }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Pilih Kategori",
                fontSize = 22.sp
            )
            IconButton(onClick = onClickManageCategory) {
                Icon(
                    Icons.Rounded.EditNote,
                    null
                )
            }
        }

        if (!uiState.isLoading && visibleCategories.isNotEmpty() && uiState.errorMessage.isNullOrEmpty())
            TabRowWithContour(
                tabs = categoryTypes.map { it.name },
                selectedTabIndex = categoryTypes.indexOfFirst { it.value == uiState.selectedType },
                onTabSelected = { index -> onClickType(categoryTypes[index].value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(visibleCategories) { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClickCategory(category.data) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedCategoryId == category.data.id,
                                onClick = { onClickCategory(category.data) },
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                category.data.name,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryPreview() {
    ArtaMiuixTheme { Content() }
}

@Preview(showBackground = true)
@Composable
private fun SelectCategoryItemsPreview() {
    ArtaMiuixTheme {
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
                        ),
                        totalAmount = 0.0,
                        transactionCount = 0,
                        transactions = emptyList()
                    ),
                    DtoCategory(
                        data = DomainCategory(
                            createdAt = "2026-05-23T10:00:00Z",
                            id = 2,
                            name = "Gaji",
                            type = "income",
                            updatedAt = "2026-05-23T10:00:00Z",
                            userId = 10,
                        ),
                        totalAmount = 0.0,
                        transactionCount = 0,
                        transactions = emptyList()
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
    ArtaMiuixTheme {
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
    ArtaMiuixTheme {
        Content(
            uiState = SelectCategoryUiState(
                errorMessage = "Terjadi kesalahan tak terduga"
            )
        )
    }
}

package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun ListCategoryScreen(vm: ListCategoryVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickCreate = { backStack.add(CategoryRoute.Upsert()) },
        onClickType = vm::onCategoryTypeSelected,
        onClickCategory = {
            backStack.add(
                CategoryRoute.ActionSheet(
                    categoryId = it.id
                )
            )
        },
        onClickBack = { backStack.removeLastOrNull() },
        onClickRetry = vm::loadCategories,
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: ListCategoryUiState = ListCategoryUiState(),
    onClickCreate: () -> Unit = {},
    onClickType: (String) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickRetry: () -> Unit = {},
) {
    val visibleCategories = when {
        uiState.selectedType == "income" -> uiState.incomeCategories
        else -> uiState.expenseCategories
    }

    MiuixTheme(
        colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = "Kelola Kategori",
                    navigationIcon = {
                        IconButton(onClick = onClickBack) {
                            Icon(
                                MiuixIcons.Back,
                                null,
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                if (!uiState.isLoading) {
                    FloatingActionButton(onClick = onClickCreate) {
                        Icon(
                            MiuixIcons.Add,
                            contentDescription = null,
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
                        }
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
                        TabRowWithContour(
                            tabs = categoryTypes.map { it.name },
                            selectedTabIndex = categoryTypes
                                .indexOfFirst { it.value == uiState.selectedType }
                                .coerceAtLeast(0),
                            onTabSelected = { index -> onClickType(categoryTypes[index].value) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 96.dp,
                            ),
                        ) {
                            item {
                                Card {
                                    visibleCategories.forEach { category ->
                                        BasicComponent(
                                            title = category.data.name,
                                            summary = when {
                                                category.data.userId == null -> "Bawaan"
                                                else -> "Kustom"
                                            },
                                            startAction = {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(end = 4.dp)
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            when (category.data.type) {
                                                                "income" -> MiuixTheme.colorScheme.primary
                                                                else -> MiuixTheme.colorScheme.error
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
                                                            "income" -> MiuixTheme.colorScheme.onPrimary
                                                            else -> MiuixTheme.colorScheme.onError
                                                        }
                                                    )
                                                }
                                            },
                                            enabled = category.data.userId != null,
                                            onClick = { onClickCategory(category.data) },
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "Kategori bawaan tidak dapat diubah atau dihapus",
                                    fontSize = 13.sp,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Category List - Default")
@Composable
private fun ListCategoryDefaultPreview() {
    Content(
        uiState = ListCategoryUiState(
            incomeCategories = listOf(
                DtoCategory(
                    data = DomainCategory(
                        createdAt = "2026-05-23T10:00:00Z",
                        id = 1,
                        name = "Gaji",
                        type = "income",
                        updatedAt = "2026-05-23T10:00:00Z",
                        userId = null,
                    ),
                    totalAmount = 0.0,
                    transactionCount = 0,
                    transactions = emptyList()
                ),
            ),
            expenseCategories = listOf(
                DtoCategory(
                    data = DomainCategory(
                        createdAt = "2026-05-23T10:00:00Z",
                        id = 2,
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
                        id = 3,
                        name = "Transport",
                        type = "expense",
                        updatedAt = "2026-05-23T10:00:00Z",
                        userId = 123,
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

@Preview(showBackground = true, name = "Category List - Loading")
@Composable
private fun ListCategoryLoadingPreview() {
    Content(
        uiState = ListCategoryUiState(
            isLoading = true
        )
    )
}

@Preview(showBackground = true, name = "Category List - Empty")
@Composable
private fun ListCategoryEmptyPreview() {
    Content()
}

@Preview(showBackground = true, name = "Category List - Error")
@Composable
private fun ListCategoryErrorPreview() {
    Content(
        uiState = ListCategoryUiState(
            errorMessage = "Terjadi kesalahan tak terduga"
        )
    )
}

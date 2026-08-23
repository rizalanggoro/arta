package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Edit
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun ListCategoryScreen(vm: ListCategoryVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickCreate = { backStack.add(CategoryRoute.Upsert()) },
        onClickType = vm::onCategoryTypeSelected,
        onClickEdit = { backStack.add(CategoryRoute.Upsert(categoryId = it.id)) },
        onClickDelete = { backStack.add(CategoryRoute.Delete(categoryId = it.id)) },
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
    onClickEdit: (DomainCategory) -> Unit = {},
    onClickDelete: (DomainCategory) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickRetry: () -> Unit = {},
) {
    val visibleCategories = when {
        uiState.selectedType == "income" -> uiState.incomeCategories
        else -> uiState.expenseCategories
    }
    var actionCategory by remember { mutableStateOf<DomainCategory?>(null) }

    ArtaMiuixTheme {
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
                        TabRow(
                            tabs = categoryTypes.map { it.name },
                            selectedTabIndex = categoryTypes
                                .indexOfFirst { it.value == uiState.selectedType }
                                .coerceAtLeast(0),
                            onTabSelected = { index -> onClickType(categoryTypes[index].value) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp)
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
                                        Box {
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
                                                onClick = { actionCategory = category.data },
                                                modifier = Modifier.fillMaxWidth(),
                                            )

                                            if (actionCategory?.id == category.data.id) {
                                                WindowListPopup(
                                                    show = true,
                                                    onDismissRequest = { actionCategory = null },
                                                    alignment = PopupPositionProvider.Align.End,
                                                ) {
                                                    ListPopupColumn {
                                                        listOf(
                                                            DropdownItem(
                                                                text = "Ubah",
                                                                icon = { iconModifier ->
                                                                    Icon(MiuixIcons.Edit, null, modifier = iconModifier)
                                                                },
                                                            ),
                                                            DropdownItem(
                                                                text = "Hapus",
                                                                icon = { iconModifier ->
                                                                    Icon(
                                                                        MiuixIcons.Delete,
                                                                        null,
                                                                        modifier = iconModifier,
                                                                        tint = MiuixTheme.colorScheme.error
                                                                    )
                                                                },
                                                            ),
                                                        ).forEachIndexed { index, item ->
                                                            DropdownImpl(
                                                                item = item,
                                                                optionSize = 2,
                                                                isSelected = false,
                                                                index = index,
                                                                onSelectedIndexChange = {
                                                                    actionCategory = null
                                                                    when (index) {
                                                                        0 -> onClickEdit(category.data)
                                                                        else -> onClickDelete(category.data)
                                                                    }
                                                                },
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
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

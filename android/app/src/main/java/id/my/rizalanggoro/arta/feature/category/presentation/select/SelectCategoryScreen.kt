package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.core.constant.toCategoryType
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.EmptyPlaceholder
import id.my.rizalanggoro.arta.shared.component.ErrorPlaceholder
import id.my.rizalanggoro.arta.shared.component.LocalBottomSheetEndAction
import id.my.rizalanggoro.arta.shared.component.LocalBottomSheetTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SelectCategoryScreen(
    selectedCategoryId: Int? = null,
    vm: SelectCategoryVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val bottomSheetTitle = LocalBottomSheetTitle.current
    val bottomSheetEndAction = LocalBottomSheetEndAction.current

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.CategorySelected>()
            .collect {
                delay(300)
                backStack.removeLastOrNull()
            }
    }

    LaunchedEffect(bottomSheetTitle, bottomSheetEndAction) {
        bottomSheetTitle?.value = "Pilih Kategori"
        bottomSheetEndAction?.value = {
            IconButton(
                onClick = {
                    backStack.removeLastOrNull()
                    backStack.add(CategoryRoute.List)
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    Icons.Rounded.EditNote,
                    null
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bottomSheetTitle?.value = null
            bottomSheetEndAction?.value = null
        }
    }

    Content(
        selectedCategoryId = selectedCategoryId,
        uiState = uiState,
        onClickRetry = vm::loadCategories,
        onClickType = vm::onCategoryTypeSelected,
        onClickCategory = vm::selectCategory,
        onClickCancel = { backStack.removeLastOrNull() },
    )
}

@Composable
private fun Content(
    selectedCategoryId: Int? = null,
    uiState: SelectCategoryUiState = SelectCategoryUiState(),
    onClickRetry: () -> Unit = {},
    onClickType: (String) -> Unit = {},
    onClickCategory: (DomainCategory) -> Unit = {},
    onClickCancel: () -> Unit = {},
) {
    val visibleCategories = uiState.categories.filter { it.data.type == uiState.selectedType }
    val maxSheetHeight = LocalConfiguration.current.screenHeightDp.dp * 0.8f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = maxSheetHeight)
    ) {
        if (!uiState.isLoading && visibleCategories.isNotEmpty() && uiState.errorMessage.isNullOrEmpty())
            TabRowWithContour(
                tabs = categoryTypes.map { it.name },
                selectedTabIndex = categoryTypes.indexOfFirst { it.value == uiState.selectedType },
                onTabSelected = { index -> onClickType(categoryTypes[index].value) },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
                }
            }

            uiState.errorMessage != null -> ErrorPlaceholder(
                modifier = Modifier.padding(16.dp),
                message = uiState.errorMessage,
                onClickRetry = onClickRetry,
            )

            visibleCategories.isEmpty() -> EmptyPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    visibleCategories.forEach { category ->
                        RadioButtonPreference(
                            title = category.data.name,
                            summary = category.data.type.toCategoryType(),
                            selected = selectedCategoryId == category.data.id,
                            onClick = { onClickCategory(category.data) },
                        )
                    }
                }
                Button(
                    onClick = onClickCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                ) {
                    Text("Batal")
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

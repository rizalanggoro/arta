package id.my.rizalanggoro.arta.feature.category.presentation.upsert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.constant.categoryTypes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.shared.component.LocalBottomSheetTitle
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TabRowWithContour
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextFieldDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun UpsertCategoryScreen(
    vm: UpsertCategoryVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val title = if (uiState.isUpdate) "Ubah Kategori" else "Buat Kategori"
    val bottomSheetTitle = LocalBottomSheetTitle.current

    LaunchedEffect(title) {
        bottomSheetTitle?.value = title
    }

    DisposableEffect(Unit) {
        onDispose { bottomSheetTitle?.value = null }
    }

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.CategoryChanged>()
            .collect { backStack.removeLastOrNull() }
    }

    Content(
        uiState = uiState,
        onChangeName = vm::onChangeName,
        onChangeType = vm::onChangeType,
        onClickSubmit = vm::submit,
    )
}

private val ErrorColor = Color(0xFFE53935)

@Composable
private fun Content(
    uiState: UpsertCategoryUiState = UpsertCategoryUiState(),
    onChangeName: (String) -> Unit = {},
    onChangeType: (String) -> Unit = {},
    onClickSubmit: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextField(
                value = uiState.name,
                onValueChange = onChangeName,
                label = "Nama kategori",
                useLabelAsPlaceholder = true,
                enabled = !uiState.isLoading,
                colors = if (uiState.nameError != null) {
                    TextFieldDefaults.textFieldColors(borderColor = ErrorColor)
                } else {
                    TextFieldDefaults.textFieldColors()
                },
            )
            uiState.nameError?.let { error ->
                Text(error, fontSize = 13.sp, color = ErrorColor)
            }
        }

        SmallTitle(
            "Tipe kategori",
            insideMargin = PaddingValues(top = 8.dp),
        )

        TabRowWithContour(
            tabs = categoryTypes.map { it.name },
            selectedTabIndex = categoryTypes
                .indexOfFirst { it.value == uiState.type }
                .coerceAtLeast(0),
            onTabSelected = { index -> onChangeType(categoryTypes[index].value) },
        )

        uiState.errorMessage?.let { error ->
            Text(error, fontSize = 13.sp, color = ErrorColor)
        }

        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
                    .height(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                InfiniteProgressIndicator()
            }

            else -> Button(
                onClick = onClickSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                Text("Simpan")
            }
        }
    }
}

@Preview(showBackground = true, name = "Category Create")
@Composable
private fun CreatePreview() {
    MiuixTheme {
        Content()
    }
}

@Preview(showBackground = true, name = "Category Update")
@Composable
private fun UpdatePreview() {
    MiuixTheme {
        Content(
            uiState = UpsertCategoryUiState(
                isUpdate = true,
                name = "Makanan",
                type = "expense",
            )
        )
    }
}

@Preview(showBackground = true, name = "Category Update - Loading")
@Composable
private fun UpdateLoadingPreview() {
    MiuixTheme {
        Content(
            uiState = UpsertCategoryUiState(
                isUpdate = true,
                isLoading = true,
                name = "Makanan",
                type = "expense",
            )
        )
    }
}

@Preview(showBackground = true, name = "Category Update - Error")
@Composable
private fun UpdateErrorPreview() {
    MiuixTheme {
        Content(
            uiState = UpsertCategoryUiState(
                isUpdate = true,
                name = "Makanan",
                type = "expense",
                nameError = "Nama kategori tidak boleh kosong",
            )
        )
    }
}

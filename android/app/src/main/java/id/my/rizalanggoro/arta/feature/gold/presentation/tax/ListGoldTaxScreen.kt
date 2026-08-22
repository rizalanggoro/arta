package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DtoGoldTaxPreference
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
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
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun ListGoldTaxScreen(
    vm: ListGoldTaxVM = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        uiState = uiState,
        onClickDelete = vm::onDeleteClicked,
        onClickBack = { backStack.removeLastOrNull() },
        onClickCreate = {
            backStack.add(
                GoldRoute.UpsertTax()
            )
        },
        onClickEdit = {
            backStack.add(
                GoldRoute.UpsertTax(
                    id = it.id
                )
            )
        },
        onClickRetry = vm::loadTaxPreferences,
    )

    if (uiState.deleteTarget != null)
        ConfirmDialog(
            title = "Hapus",
            description = "Apakah Anda yakin akan menghapus preferensi pajak untuk karat " +
                    "${uiState.deleteTarget!!.carat}? Tindakan ini tidak dapat dipulihkan",
            onDismissRequest = vm::onDialogDismissed,
            onConfirmRequest = vm::onDeleteClicked,
            isLoading = uiState.isDeleting,
            confirmText = "Hapus"
        )
}

@Composable
private fun Content(
    uiState: ListGoldTaxUiState = ListGoldTaxUiState(),
    onClickCreate: () -> Unit = {},
    onClickEdit: (DtoGoldTaxPreference) -> Unit = {},
    onClickDelete: (DtoGoldTaxPreference) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickRetry: () -> Unit = {},
) {
    MiuixTheme(
        colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = "Pajak Emas",
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
                if (uiState.isLoading.not() && uiState.errorMessage.isNullOrBlank())
                    FloatingActionButton(onClick = dropUnlessResumed { onClickCreate() }) {
                        Icon(
                            MiuixIcons.Add,
                            null
                        )
                    }
            }
        ) { paddingValues ->
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    InfiniteProgressIndicator(color = MiuixTheme.colorScheme.primary)
                }

                uiState.errorMessage != null -> ErrorPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    message = uiState.errorMessage,
                    onClickRetry = onClickRetry
                )

                uiState.preferences.isEmpty() -> EmptyPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 96.dp,
                    ),
                ) {
                    item {
                        Card {
                            uiState.preferences.forEach { preference ->
                                BasicComponent(
                                    title = "Karat ${preference.carat}",
                                    summary = "Rasio pajak ${preference.taxRate}%",
                                    endActions = {
                                        IconButton(onClick = { onClickEdit(preference) }) {
                                            Icon(
                                                Icons.Rounded.Edit,
                                                null
                                            )
                                        }
                                        IconButton(onClick = { onClickDelete(preference) }) {
                                            Icon(
                                                Icons.Rounded.Delete,
                                                null
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Content(
        uiState = ListGoldTaxUiState(
            isLoading = false,
            preferences = listOf(
                DtoGoldTaxPreference(
                    id = 1,
                    userId = 1,
                    carat = 24.0,
                    taxRate = 5.0,
                    createdAt = "",
                    updatedAt = ""
                ),
                DtoGoldTaxPreference(
                    id = 2,
                    userId = 1,
                    carat = 18.0,
                    taxRate = 3.5,
                    createdAt = "",
                    updatedAt = ""
                ),
            )
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    Content(
        uiState = ListGoldTaxUiState(
            isLoading = true,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    Content(
        uiState = ListGoldTaxUiState(
            errorMessage = "Terjadi kesalahan tak terduga"
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    Content(
        uiState = ListGoldTaxUiState(
            preferences = emptyList()
        ),
    )
}

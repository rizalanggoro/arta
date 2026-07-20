package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    uiState: ListGoldTaxUiState = ListGoldTaxUiState(),
    onClickCreate: () -> Unit = {},
    onClickEdit: (DtoGoldTaxPreference) -> Unit = {},
    onClickDelete: (DtoGoldTaxPreference) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickRetry: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pajak Emas") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        floatingActionButton = {
            if (uiState.isLoading.not() && uiState.errorMessage.isNullOrBlank())
                FloatingActionButton(onClick = dropUnlessResumed { onClickCreate() }) {
                    Icon(
                        Icons.Rounded.Add,
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
                LoadingIndicator()
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
                    .padding(paddingValues)
            ) {
                itemsIndexed(uiState.preferences) { index, preference ->
                    val shape = RoundedCornerShape(
                        topStart = if (index == 0) 16.dp else 4.dp,
                        topEnd = if (index == 0) 16.dp else 4.dp,
                        bottomStart = if (index == uiState.preferences.lastIndex) 16.dp else 4.dp,
                        bottomEnd = if (index == uiState.preferences.lastIndex) 16.dp else 4.dp,
                    )
                    ListItem(
                        headlineContent = {
                            Text("Karat ${preference.carat}")
                        },
                        supportingContent = {
                            Text(text = "Rasio pajak ${preference.taxRate}%")
                        },
                        trailingContent = {
                            Row {
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
                            }
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = if (index == 0) 0.dp else 2.dp)
                            .clip(shape)
                    )
//                    Card(modifier = Modifier.fillMaxWidth()) {
//                        Column(
//                            modifier = Modifier.padding(16.dp),
//                            verticalArrangement = Arrangement.spacedBy(12.dp),
//                        ) {
//                            Text(
//                                text = "Karat ${preference.carat}",
//                                style = MaterialTheme.typography.titleMedium,
//                            )
//                            Text(
//                                text = "Rasio pajak ${preference.taxRate}%",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.outline,
//                            )
//                            OutlinedButton(
//                                onClick = { onClickEdit(preference) },
//                                modifier = Modifier.fillMaxWidth(),
//                            ) {
//                                Icon(Icons.Rounded.Edit, contentDescription = null)
//                                Text("Ubah")
//                            }
//                            OutlinedButton(
//                                onClick = { onClickDelete(preference) },
//                                modifier = Modifier.fillMaxWidth(),
//                            ) {
//                                Icon(Icons.Rounded.Delete, contentDescription = null)
//                                Text("Hapus")
//                            }
//                        }
//                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ArtaTheme {
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
}

@Preview(showBackground = true)
@Composable
private fun LoadingPreview() {
    ArtaTheme {
        Content(
            uiState = ListGoldTaxUiState(
                isLoading = true,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    ArtaTheme {
        Content(
            uiState = ListGoldTaxUiState(
                errorMessage = "Terjadi kesalahan tak terduga"
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyPreview() {
    ArtaTheme {
        Content(
            uiState = ListGoldTaxUiState(
                preferences = emptyList()
            ),
        )
    }
}
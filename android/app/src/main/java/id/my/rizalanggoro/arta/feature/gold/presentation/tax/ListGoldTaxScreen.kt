package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.domain.GoldTaxPreference
import id.my.rizalanggoro.arta.feature.gold.presentation.tax.component.DeleteGoldTaxConfirmationDialog
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme
import kotlinx.coroutines.flow.filterIsInstance

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ListGoldTaxScreen(
    vm: ListGoldTaxVM = viewModel(factory = ListGoldTaxVM.Factory),
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        AppEventBus.event
            .filterIsInstance<AppEvent.GoldTaxChanged>()
            .collect { vm.loadTaxPreferences() }
    }

    Content(
        snackbarHostState = snackbarHostState,
        uiState = uiState,
        onClickDelete = vm::onDeleteRequested,
        onClickBack = { backStack.removeLastOrNull() },
        onClickCreate = {
            backStack.add(
                Routes.UpsertGoldTaxRoute()
            )
        },
        onClickEdit = { preference ->
            backStack.add(
                Routes.UpsertGoldTaxRoute(
                    id = preference.id
                )
            )
        },
    )

    uiState.deleteTarget?.let { preference ->
        DeleteGoldTaxConfirmationDialog(
            preference = preference,
            onDismiss = vm::dismissDeleteDialog,
            onConfirm = { vm.confirmDeleteTaxPreference(preference) },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Content(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    uiState: ListGoldTaxUiState = ListGoldTaxUiState(),
    onClickCreate: () -> Unit = {},
    onClickEdit: (GoldTaxPreference) -> Unit = {},
    onClickDelete: (GoldTaxPreference) -> Unit = {},
    onClickBack: () -> Unit = {},
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Pajak emas") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        floatingActionButton = {
            if (uiState.isLoading.not())
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

            uiState.preferences.isEmpty() ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Rounded.Inbox,
                        null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                    )
                    Text(
                        text = "Belum ada preferensi pajak",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                    )

                }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(uiState.preferences) { preference ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = "Karat ${preference.carat}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "Rasio pajak ${preference.taxRate}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                            )
                            OutlinedButton(
                                onClick = { onClickEdit(preference) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Rounded.Edit, contentDescription = null)
                                Text("Ubah")
                            }
                            OutlinedButton(
                                onClick = { onClickDelete(preference) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = null)
                                Text("Hapus")
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
private fun PreviewListGoldTaxScreen() {
    ArtaTheme {
        Content(
            uiState = ListGoldTaxUiState(
                isLoading = false,
                preferences = listOf(
                    GoldTaxPreference(id = 1, carat = 24.0, taxRate = 5.0),
                    GoldTaxPreference(id = 2, carat = 18.0, taxRate = 3.5),
                )
            ),
        )
    }
}
package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainGold
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import id.my.rizalanggoro.arta.shared.component.ConfirmDialog
import kotlinx.coroutines.flow.filterIsInstance
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.SnackbarHost
import top.yukonga.miuix.kmp.basic.SnackbarHostState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton

@Composable
fun GoldDetailScreen(goldId: Int) {
    val viewModel: GoldDetailVM = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack = LocalBackStack.current

    LaunchedEffect(goldId) {
        viewModel.load(goldId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GoldDetailEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                is GoldDetailEffect.NavigateToEdit -> backStack.add(
                    GoldRoute.Upsert(
                        goldId = effect.goldId
                    )
                )

                GoldDetailEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    LaunchedEffect(goldId) {
        AppEventBus.event
            .filterIsInstance<AppEvent.GoldChanged>()
            .collect { viewModel.load(goldId) }
    }

    Content(
        snackbarHostState = snackbarHostState,
        gold = uiState.gold,
        isLoading = uiState.isLoading,
        showDeleteDialog = uiState.showDeleteDialog,
        onClickBack = { backStack.removeLastOrNull() },
        onEdit = viewModel::onEditClicked,
        onDeleteRequested = viewModel::onDeleteRequested,
        onDismissDelete = viewModel::dismissDeleteDialog,
        onConfirmDelete = viewModel::confirmDelete,
    )
}

@Composable
private fun Content(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    gold: DomainGold? = null,
    isLoading: Boolean = false,
    showDeleteDialog: Boolean = false,
    onClickBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDeleteRequested: () -> Unit = {},
    onDismissDelete: () -> Unit = {},
    onConfirmDelete: () -> Unit = {},
) {
    Scaffold(
        snackbarHost = { SnackbarHost(state = snackbarHostState) },
        topBar = {
            SmallTopAppBar(
                title = "Detail Emas",
                navigationIcon = {
                    TextButton(
                        text = "Kembali",
                        onClick = onClickBack
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (isLoading && gold == null) {
                Text(text = "Memuat...")
                return@Column
            }

            if (gold == null) {
                Text(text = "Data emas tidak ditemukan")
                return@Column
            }

            Text(text = "Tanggal: ${gold.date}")
            Text(text = "Berat (gram): ${gold.grams}")
            Text(text = "Harga total: ${gold.price}")
            Text(text = "Tipe: ${gold.type}")
            Text(text = "Karat: ${gold.carat}")
            Text(text = "Catatan: ${gold.notes}")

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text(text = "Edit")
                }
                Button(
                    onClick = onDeleteRequested,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColorsPrimary()
                ) {
                    Text(text = "Hapus")
                }
            }
        }

        if (showDeleteDialog) {
            ConfirmDialog(
                title = "Hapus data emas",
                description = "Anda yakin ingin menghapus data emas ini?",
                confirmText = "Hapus",
                onDismissRequest = onDismissDelete,
                onConfirmRequest = onConfirmDelete,
            )
        }
    }
}

@Preview(showBackground = true, name = "Gold Detail - Default")
@Composable
private fun GoldDetailScreenPreview() {
    ArtaMiuixTheme {
        Content(
            gold = DomainGold(
                id = 1,
                walletId = 2,
                date = "2026-05-16",
                grams = 10.0,
                price = 9000000.0,
                type = "pure_gold",
                carat = 24.0,
                notes = "Contoh catatan",
                createdAt = "",
                updatedAt = "",
            ),
        )
    }
}

@Preview(showBackground = true, name = "Gold Detail - Loading")
@Composable
private fun GoldDetailLoadingPreview() {
    ArtaMiuixTheme {
        Content(
            snackbarHostState = remember { SnackbarHostState() },
            isLoading = true,
        )
    }
}

package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import id.my.rizalanggoro.arta.core.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldDetailScreen(goldId: Int) {
    val viewModel: GoldDetailVM = viewModel(factory = GoldDetailVM.Factory)
    val uiState by viewModel.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        viewModel.load(goldId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GoldDetailEffect.ShowMessage -> {
                    // TODO: show snackbar (omitted for brevity)
                }
                is GoldDetailEffect.NavigateBack -> backStack.removeLastOrNull()
                is GoldDetailEffect.NavigateToEdit -> backStack.add(Routes.GoldFormRoute(goldId = effect.goldId))
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TopAppBar(title = { Text(text = "Detail Emas") }, modifier = Modifier.fillMaxWidth(), scrollBehavior = null)

        Spacer(modifier = Modifier.height(8.dp))

        val gold = uiState.gold
        if (gold == null) {
            Text(text = "Memuat...")
            return@Column
        }

        Text(text = "Tanggal: ${gold.date}")
        Text(text = "Berat (gram): ${gold.grams}")
        Text(text = "Harga/gram: ${gold.pricePerGram}")
        Text(text = "Total nilai: ${gold.totalValue}")
        Text(text = "Tipe: ${gold.type}")
        Text(text = "Kemurnian: ${gold.purityPercent}")
        Text(text = "Catatan: ${gold.notes}")

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { viewModel.onEditClicked() }, modifier = Modifier.weight(1f)) {
                Text(text = "Edit")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.onDeleteRequested() }, modifier = Modifier.weight(1f)) {
                Text(text = "Hapus")
            }
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            confirmButton = {
                Button(onClick = { viewModel.confirmDelete() }) { Text(text = "Hapus") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteDialog() }) { Text(text = "Batal") }
            },
            title = { Text(text = "Hapus data emas") },
            text = { Text(text = "Anda yakin ingin menghapus data emas ini?") },
        )
    }
}

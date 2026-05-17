package id.my.rizalanggoro.arta.feature.transaction.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.Routes
import androidx.compose.ui.tooling.preview.Preview
import id.my.rizalanggoro.arta.domain.Transaction

@Composable
fun TransactionDetailScreen(
    transactionId: Int,
    vm: TransactionDetailVM = viewModel(factory = TransactionDetailVM.Factory),
) {
    val tx by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(transactionId) { vm.load(transactionId) }

    Content(
        tx = tx,
        onEdit = { t -> backStack.add(Routes.TransactionFormRoute(transactionId = t.id, walletId = t.walletId)) }
    )
}

@Composable
private fun Content(tx: Transaction?, onEdit: (Transaction) -> Unit = {}) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (tx == null) {
                Text(text = "Memuat transaksi...", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(text = "Tipe: ${tx.type}")
                Text(text = "Jumlah: ${tx.amount}")
                Text(text = "Kategori ID: ${tx.categoryId}")
                Text(text = "Deskripsi: ${tx.description}")
                Text(text = "Tanggal: ${tx.date}")

                Button(onClick = { onEdit(tx) }) {
                    Text("Ubah")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionDetailScreenPreview() {
    Content(tx = Transaction(id = 1, walletId = 1, type = "income", amount = 100000.0, categoryId = 1, description = "Contoh transaksi", date = "2026-05-16"))
}

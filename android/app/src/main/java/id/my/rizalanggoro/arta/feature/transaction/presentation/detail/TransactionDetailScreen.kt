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
import androidx.hilt.navigation.compose.hiltViewModel
import id.my.rizalanggoro.arta.core.LocalBackStack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.core.Routes
import id.my.rizalanggoro.arta.core.Routes.TransactionUpdateRoute
import androidx.compose.ui.tooling.preview.Preview
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun TransactionDetailScreen(
    transactionId: Int,
    vm: TransactionDetailVM = hiltViewModel(),
) {
    val tx by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(transactionId) { vm.load(transactionId) }

    Content(
        tx = tx,
        onEdit = { t -> backStack.add(TransactionUpdateRoute(transactionId = t.id)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(tx: DomainTransaction?, onEdit: (DomainTransaction) -> Unit = {}) {
    androidx.compose.material3.Scaffold(
        topBar = { androidx.compose.material3.TopAppBar(title = { Text("Detail Transaksi") }) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (tx == null) {
                        Text(text = "Memuat transaksi...", style = MaterialTheme.typography.bodyMedium)
                    } else {
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
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionDetailScreenPreview() {
    Content(tx = DomainTransaction(amount = java.math.BigDecimal(100000), categoryId = 1, createdAt = "", date = "2026-05-16", description = "Contoh transaksi", id = 1, updatedAt = "", walletId = 1))
}

@Preview(showBackground = true, name = "Transaction Detail - Loading")
@Composable
private fun TransactionDetailLoadingPreview() {
    Content(tx = null)
}

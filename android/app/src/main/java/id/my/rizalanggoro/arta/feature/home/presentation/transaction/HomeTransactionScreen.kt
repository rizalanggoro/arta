package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeTransactionScreen(vm: TransactionListVM = hiltViewModel()) {
    val uiState by vm.uiState.collectAsState()

    Content(
        title = uiState.title,
        description = uiState.description,
        transactions = uiState.transactions,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onRetry = vm::loadTransactions,
    )
}

@Composable
private fun Content(
    title: String,
    description: String,
    transactions: List<DomainTransaction>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Text(text = description)
                }
            }
        }

        item {
            if (isLoading) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator()
                    Text(text = "Memuat transaksi...")
                }
            } else if (!errorMessage.isNullOrBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            } else if (transactions.isEmpty()) {
                Text(text = "Belum ada transaksi.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        items(transactions) { tx ->
            TransactionRow(transaction = tx)
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: DomainTransaction,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = transaction.description.ifBlank { "Transaksi #${transaction.id}" }, style = MaterialTheme.typography.titleSmall)
            Text(text = transaction.date, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = "Rp ${transaction.amount.toLong()}", style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(showBackground = true, name = "Transaction List")
@Composable
private fun HomeTransactionListPreview() {
    ArtaTheme {
        Content(
            title = "Transaksi",
            description = "Daftar transaksi terbaru.",
            transactions = listOf(),
            isLoading = false,
            errorMessage = null,
        )
    }
}

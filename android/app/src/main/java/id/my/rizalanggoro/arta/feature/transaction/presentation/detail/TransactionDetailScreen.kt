package id.my.rizalanggoro.arta.feature.transaction.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.my.rizalanggoro.arta.core.application.route.TransactionRoute
import id.my.rizalanggoro.arta.core.extension.toFormattedDate
import id.my.rizalanggoro.arta.core.extension.toIndonesianCurrency
import id.my.rizalanggoro.arta.core.utils.LocalBackStack
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import id.my.rizalanggoro.arta.shared.component.ArtaMiuixTheme
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun TransactionDetailScreen(
    transactionId: Int,
    vm: TransactionDetailVM,
) {
    val uiState by vm.uiState.collectAsState()
    val backStack = LocalBackStack.current

    Content(
        transaction = uiState.transaction,
        category = uiState.category,
        onClickBack = { backStack.removeLastOrNull() },
        onEdit = { t ->
            backStack.add(
                TransactionRoute.Upsert(transactionId = t.id)
            )
        },
        onDelete = { t ->
            backStack.add(
                TransactionRoute.Delete(transactionId = t.id)
            )
        }
    )
}

@Composable
private fun Content(
    transaction: DomainTransaction? = null,
    category: DomainCategory? = null,
    onClickBack: () -> Unit = {},
    onEdit: (DomainTransaction) -> Unit = {},
    onDelete: (DomainTransaction) -> Unit = {},
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "Detail Transaksi",
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (transaction == null) {
                Text(
                    text = "Memuat transaksi...",
                    fontSize = 14.sp
                )
                return@Column
            }

            val isIncome = category?.type == "income"

            // Amount summary card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MiuixTheme.colorScheme.secondaryContainer)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        if (isIncome) Icons.AutoMirrored.Rounded.CallReceived
                        else Icons.AutoMirrored.Rounded.CallMade,
                        null,
                        tint = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isIncome) "Pemasukan" else "Pengeluaran",
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = transaction.amount.toIndonesianCurrency(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MiuixTheme.colorScheme.onSecondaryContainer
                )
            }

            // Detail rows grouped in a card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MiuixTheme.colorScheme.surfaceContainer)
            ) {
                DetailRow(label = "Kategori", value = category?.name ?: "-")
                DetailRow(label = "Tanggal", value = transaction.date.toFormattedDate())
                DetailRow(label = "Deskripsi", value = transaction.description.ifEmpty { "-" })
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEdit(transaction) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ubah")
                }
                Button(
                    onClick = { onDelete(transaction) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColorsPrimary()
                ) {
                    Text("Hapus")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, name = "Transaction Detail - Income")
@Composable
private fun TransactionDetailIncomePreview() {
    ArtaMiuixTheme {
        Content(
            transaction = DomainTransaction(
                amount = 100000.0,
                categoryId = 1,
                createdAt = "2026-05-16T10:00:00+07:00",
                date = "2026-05-16T10:00:00+07:00",
                description = "Gaji bulanan",
                id = 1,
                updatedAt = "2026-05-16T10:00:00+07:00",
                walletId = 1
            ),
            category = DomainCategory(
                id = 1,
                name = "Gaji",
                type = "income",
                createdAt = "",
                updatedAt = ""
            )
        )
    }
}

@Preview(showBackground = true, name = "Transaction Detail - Expense")
@Composable
private fun TransactionDetailExpensePreview() {
    ArtaMiuixTheme {
        Content(
            transaction = DomainTransaction(
                amount = 50000.0,
                categoryId = 2,
                createdAt = "2026-05-16T10:00:00+07:00",
                date = "2026-05-16T10:00:00+07:00",
                description = "Makan siang",
                id = 2,
                updatedAt = "2026-05-16T10:00:00+07:00",
                walletId = 1
            ),
            category = DomainCategory(
                id = 2,
                name = "Makanan",
                type = "expense",
                createdAt = "",
                updatedAt = ""
            )
        )
    }
}

@Preview(showBackground = true, name = "Transaction Detail - Loading")
@Composable
private fun TransactionDetailLoadingPreview() {
    ArtaMiuixTheme {
        Content(transaction = null)
    }
}

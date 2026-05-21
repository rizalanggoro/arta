package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeGoldScreen(
    vm: HomeGoldVM = viewModel(factory = HomeGoldVM.Factory),
    onClickManageTax: () -> Unit = {},
) {
    val uiState by vm.uiState.collectAsState()

    Content(
        golds = uiState.golds,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onClickManageTax = onClickManageTax,
    )
}

@Composable
private fun Content(
    golds: List<Gold> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onClickManageTax: () -> Unit = {},
) {
    when {
        isLoading -> Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TaxActions(onClickManageTax = onClickManageTax)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LoadingIndicator()
                }
            }
        }

        golds.isEmpty() -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TaxActions(onClickManageTax = onClickManageTax)
            Icon(
                Icons.Rounded.Inbox,
                null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outlineVariant,
            )
            Text(
                "Belum ada emas yang ditambahkan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                TaxActions(onClickManageTax = onClickManageTax)
            }
            items(golds) { gold ->
                GoldItem(gold = gold)
            }
        }
    }
}

@Composable
private fun TaxActions(
    onClickManageTax: () -> Unit,
) {
    OutlinedButton(onClick = onClickManageTax, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Rounded.Edit, contentDescription = null)
        Text(text = "Kelola pajak emas")
    }
}

@Composable
private fun GoldItem(gold: Gold) {
    OutlinedCard {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = "Senin, 12 Juni 2023",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Rp 5.000.000",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "3.3 gram",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            HorizontalDivider()
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Harga beli Rp 0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    "Keuntungan Rp 0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Gold List")
@Composable
private fun HomeGoldListPreview() {
    ArtaTheme {
        Content(
            golds = List(5) {
                Gold(
                    id = it,
                    walletId = 1,
                    date = "2026-05-20",
                    grams = 1.0,
                    price = 7800000.0,
                    type = "pure_gold",
                    carat = 24.0,
                )
            },
            isLoading = false,
            errorMessage = null,
        )
    }
}

@Preview(showBackground = true, name = "Gold List Loading")
@Composable
private fun HomeGoldListLoadingPreview() {
    ArtaTheme {
        Content(
            isLoading = true
        )
    }
}

@Preview(showBackground = true, name = "Gold List Empty")
@Composable
private fun HomeGoldListEmptyPreview() {
    ArtaTheme {
        Content(
            golds = emptyList(),
        )
    }
}

@Preview(showBackground = true, name = "Gold List Error")
@Composable
private fun HomeGoldListErrorPreview() {
    ArtaTheme {
        Content(
            golds = emptyList(),
            isLoading = false,
            errorMessage = "Terjadi kesalahan",
        )
    }
}

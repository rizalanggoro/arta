package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import id.my.rizalanggoro.arta.domain.Gold
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeGoldListContent(
    modifier: Modifier = Modifier,
    onClickCreateGold: () -> Unit = {},
) {
    val vm: GoldListVM = viewModel(factory = GoldListVM.Factory)
    val uiState by vm.uiState.collectAsState()
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(text = uiState.description)

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage ?: "")
            } else {
                LazyColumn {
                    items(uiState.golds) { gold ->
                        GoldListRow(gold = gold)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onClickCreateGold) {
                Text("Buat data emas")
            }
        }
    }
}

@Composable
private fun GoldListRow(gold: Gold, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = gold.date, style = MaterialTheme.typography.titleSmall)
            Text(text = "${gold.grams} g", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = "Rp ${gold.price}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true, name = "Gold List")
@Composable
private fun HomeGoldListPreview() {
    ArtaTheme {
        HomeGoldListContent()
    }
}

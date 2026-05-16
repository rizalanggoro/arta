package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.my.rizalanggoro.arta.ui.theme.ArtaTheme

@Composable
fun HomeGoldDashboardContent(modifier: Modifier = Modifier) {
    val vm: GoldDashboardVM = viewModel(factory = GoldDashboardVM.Factory)
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
            Text(text = "Saldo: ${uiState.balanceInGram}")
            Text(text = uiState.description)
        }
    }
}

@Preview(showBackground = true, name = "Dashboard - Tabungan Emas")
@Composable
private fun HomeGoldDashboardPreview() {
    ArtaTheme {
        HomeGoldDashboardContent()
    }
}

package id.my.rizalanggoro.arta.feature.home.presentation.chart

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
fun HomeChartScreen(vm: ChartVM = viewModel(factory = ChartVM.Factory)) {
    val uiState by vm.uiState.collectAsState()

    Content(
        title = uiState.title,
        description = uiState.description,
    )
}

@Composable
private fun Content(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(text = description)
        }
    }
}

@Preview(showBackground = true, name = "Chart")
@Composable
private fun HomeChartPreview() {
    ArtaTheme {
        Content(
            title = "Chart",
            description = "Visualisasi transaksi dan pola pengeluaran.",
        )
    }
}

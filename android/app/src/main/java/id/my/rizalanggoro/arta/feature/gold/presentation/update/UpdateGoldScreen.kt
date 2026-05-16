package id.my.rizalanggoro.arta.feature.gold.presentation.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
fun UpdateGoldScreen(goldId: Int) {
    val viewModel: UpdateGoldVM = viewModel(factory = UpdateGoldVM.Factory)
    val uiState by viewModel.uiState.collectAsState()
    val backStack = LocalBackStack.current

    LaunchedEffect(Unit) {
        viewModel.load(goldId)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UpdateGoldEffect.ShowMessage -> {
                    // TODO: show snackbar
                }
                is UpdateGoldEffect.NavigateBack -> backStack.removeLastOrNull()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TopAppBar(title = { Text(text = "Ubah Emas") })

        OutlinedTextField(value = uiState.date, onValueChange = { viewModel.onDateChanged(it) }, label = { Text(text = "Tanggal (ISO)") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = uiState.grams, onValueChange = { viewModel.onGramsChanged(it) }, label = { Text(text = "Gram") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = uiState.pricePerGram, onValueChange = { viewModel.onPricePerGramChanged(it) }, label = { Text(text = "Harga per gram") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = uiState.type, onValueChange = { viewModel.onTypeChanged(it) }, label = { Text(text = "Tipe") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = uiState.purityPercent, onValueChange = { viewModel.onPurityPercentChanged(it) }, label = { Text(text = "Kemurnian (%)") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = uiState.notes, onValueChange = { viewModel.onNotesChanged(it) }, label = { Text(text = "Catatan") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

        Button(onClick = { viewModel.updateGold() }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text(text = "Simpan Perubahan")
        }
    }
}

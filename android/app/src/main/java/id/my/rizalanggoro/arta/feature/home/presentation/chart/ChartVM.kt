package id.my.rizalanggoro.arta.feature.home.presentation.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChartVM : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer { ChartVM() }
        }
    }

    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()
}

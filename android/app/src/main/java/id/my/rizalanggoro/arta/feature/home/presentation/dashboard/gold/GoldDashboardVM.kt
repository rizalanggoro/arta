package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoldDashboardVM : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                GoldDashboardVM()
            }
        }
    }

    private val _uiState = MutableStateFlow(GoldDashboardUiState())
    val uiState: StateFlow<GoldDashboardUiState> = _uiState.asStateFlow()
}

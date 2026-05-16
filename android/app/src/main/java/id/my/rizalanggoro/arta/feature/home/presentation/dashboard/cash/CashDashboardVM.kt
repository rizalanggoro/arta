package id.my.rizalanggoro.arta.feature.home.presentation.dashboard.cash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CashDashboardVM : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                CashDashboardVM()
            }
        }
    }

    private val _uiState = MutableStateFlow(CashDashboardUiState())
    val uiState: StateFlow<CashDashboardUiState> = _uiState.asStateFlow()
}

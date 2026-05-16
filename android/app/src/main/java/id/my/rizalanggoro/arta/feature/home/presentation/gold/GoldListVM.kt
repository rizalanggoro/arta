package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoldListVM : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer { GoldListVM() }
        }
    }

    private val _uiState = MutableStateFlow(GoldListUiState())
    val uiState: StateFlow<GoldListUiState> = _uiState.asStateFlow()
}

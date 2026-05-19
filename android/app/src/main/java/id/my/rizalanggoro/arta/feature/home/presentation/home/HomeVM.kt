package id.my.rizalanggoro.arta.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeVM(
    private val selectedWalletPrefs: SelectedWalletPrefs
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                HomeVM(
                    selectedWalletPrefs = app.selectedWalletPrefs
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onDestinationSelected(index: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedIndex = index.coerceAtLeast(0))
        }
    }

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update {
                    it.copy(
                        selectedWallet = wallet
                    )
                }
            }
        }
    }
}
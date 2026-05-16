package id.my.rizalanggoro.arta.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeVM : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				HomeVM()
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

	fun onWalletTypeChanged(walletType: HomeWalletType) {
		_uiState.update { currentState ->
			if (currentState.walletType == walletType) {
				currentState
			} else {
				currentState.copy(walletType = walletType, selectedIndex = 0)
			}
		}
	}
}
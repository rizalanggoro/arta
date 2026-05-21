package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.domain.GoldTaxPreference
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListGoldTaxVM(
	private val goldRepository: GoldRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val app = this[APPLICATION_KEY] as MyApplication
				ListGoldTaxVM(goldRepository = app.goldRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(ListGoldTaxUiState())
	val uiState: StateFlow<ListGoldTaxUiState> = _uiState.asStateFlow()

	fun loadTaxPreferences() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			goldRepository.getTaxPreferences()
				.onSuccess { preferences ->
					_uiState.update {
						it.copy(
							preferences = preferences,
							deleteTarget = null,
							isLoading = false,
							errorMessage = null,
						)
					}
				}
				.onFailure { throwable ->
					_uiState.update {
						it.copy(
							isLoading = false,
							errorMessage = throwable.message ?: "Gagal memuat preferensi pajak",
						)
					}
				}
		}
	}

	fun onDeleteRequested(preference: GoldTaxPreference) {
		_uiState.update { it.copy(deleteTarget = preference) }
	}

	fun dismissDeleteDialog() {
		_uiState.update { it.copy(deleteTarget = null) }
	}

	fun confirmDeleteTaxPreference(preference: GoldTaxPreference) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			goldRepository.deleteTaxPreference(preference.id)
				.onSuccess {
					_uiState.update { it.copy(isLoading = false, deleteTarget = null) }
					loadTaxPreferences()
				}
				.onFailure { throwable ->
					_uiState.update {
						it.copy(
							isLoading = false,
							errorMessage = throwable.message ?: "Gagal menghapus preferensi pajak",
						)
					}
				}
		}
	}
}
package id.my.rizalanggoro.arta.feature.gold.presentation.updatetax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.domain.GoldTaxPreference
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateGoldTaxVM(
	private val goldRepository: GoldRepository,
	private val taxPreferenceId: Int,
) : ViewModel() {
	companion object {
		fun Factory(taxPreferenceId: Int) = viewModelFactory {
			initializer {
				val app = this[APPLICATION_KEY] as MyApplication
				UpdateGoldTaxVM(goldRepository = app.goldRepository, taxPreferenceId = taxPreferenceId)
			}
		}
	}

	private val _uiState = MutableStateFlow(UpdateGoldTaxUiState())
	val uiState: StateFlow<UpdateGoldTaxUiState> = _uiState.asStateFlow()

	private val _effect = MutableSharedFlow<UpdateGoldTaxEffect>()
	val effect: SharedFlow<UpdateGoldTaxEffect> = _effect.asSharedFlow()

	fun load() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			goldRepository.getTaxPreferences()
				.onSuccess { preferences ->
					val selected = preferences.firstOrNull { it.id == taxPreferenceId }
					if (selected == null) {
						_uiState.update {
							it.copy(
								isLoading = false,
								errorMessage = "Preferensi pajak tidak ditemukan",
							)
						}
						_effect.emit(UpdateGoldTaxEffect.ShowMessage("Preferensi pajak tidak ditemukan"))
						return@launch
					}

					_uiState.update {
						it.copy(
							preferences = preferences,
							carat = selected.carat.toString(),
							taxRate = selected.taxRate.toString(),
							isLoading = false,
							errorMessage = null,
							caratError = null,
							taxRateError = null,
						)
					}
				}
				.onFailure { throwable ->
					_uiState.update { it.copy(isLoading = false) }
					_effect.emit(UpdateGoldTaxEffect.ShowMessage(throwable.message ?: "Gagal memuat preferensi pajak"))
				}
		}
	}

	fun onCaratChanged(value: String) {
		_uiState.update { it.copy(carat = value, caratError = null) }
	}

	fun onTaxRateChanged(value: String) {
		_uiState.update { it.copy(taxRate = value, taxRateError = null) }
	}

	fun updateTaxPreference() {
		val current = _uiState.value
		val carat = current.carat.trim().toDoubleOrNull()
		val taxRate = current.taxRate.trim().toDoubleOrNull()
		var hasError = false
		var caratError: String? = null
		var taxRateError: String? = null

		if (carat == null || carat <= 0 || carat > 24) {
			caratError = "Karat harus antara 0 dan 24"
			hasError = true
		}

		if (taxRate == null || taxRate < 0 || taxRate > 100) {
			taxRateError = "Rasio pajak harus antara 0 dan 100"
			hasError = true
		}

		if (hasError) {
			_uiState.update {
				it.copy(
					caratError = caratError,
					taxRateError = taxRateError,
				)
			}
			viewModelScope.launch {
				_effect.emit(UpdateGoldTaxEffect.ShowMessage("Periksa kembali isian pajak"))
			}
			return
		}

		val preferences = current.preferences.toMutableList()
		val index = preferences.indexOfFirst { it.id == taxPreferenceId }
		if (index < 0) {
			viewModelScope.launch {
				_effect.emit(UpdateGoldTaxEffect.ShowMessage("Preferensi pajak tidak ditemukan"))
			}
			return
		}

		if (preferences.any { it.id != taxPreferenceId && it.carat == carat }) {
			_uiState.update { it.copy(caratError = "Karat tidak boleh duplikat") }
			viewModelScope.launch {
				_effect.emit(UpdateGoldTaxEffect.ShowMessage("Karat sudah digunakan"))
			}
			return
		}

		val validatedCarat = carat ?: return
		val validatedTaxRate = taxRate ?: return

		viewModelScope.launch {
			_uiState.update { it.copy(isSaving = true) }
			goldRepository.updateTaxPreference(
				preferenceId = taxPreferenceId,
				preference = GoldTaxPreference(id = taxPreferenceId, carat = validatedCarat, taxRate = validatedTaxRate),
			)
				.onSuccess {
					_uiState.update {
						it.copy(
							isSaving = false,
							caratError = null,
							taxRateError = null,
						)
					}
					_effect.emit(UpdateGoldTaxEffect.ShowMessage("Preferensi pajak berhasil diperbarui"))
					_effect.emit(UpdateGoldTaxEffect.NavigateBack)
				}
				.onFailure { throwable ->
					_uiState.update { it.copy(isSaving = false) }
					_effect.emit(UpdateGoldTaxEffect.ShowMessage(throwable.message ?: "Gagal memperbarui preferensi pajak"))
				}
		}
	}
}

sealed interface UpdateGoldTaxEffect {
	data class ShowMessage(val message: String) : UpdateGoldTaxEffect
	data object NavigateBack : UpdateGoldTaxEffect
}
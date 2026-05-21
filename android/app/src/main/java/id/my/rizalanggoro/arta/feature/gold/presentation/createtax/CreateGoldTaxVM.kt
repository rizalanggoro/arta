package id.my.rizalanggoro.arta.feature.gold.presentation.createtax

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

class CreateGoldTaxVM(
	private val goldRepository: GoldRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val app = this[APPLICATION_KEY] as MyApplication
				CreateGoldTaxVM(goldRepository = app.goldRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(CreateGoldTaxUiState())
	val uiState: StateFlow<CreateGoldTaxUiState> = _uiState.asStateFlow()

	private val _effect = MutableSharedFlow<CreateGoldTaxEffect>()
	val effect: SharedFlow<CreateGoldTaxEffect> = _effect.asSharedFlow()

	fun onCaratChanged(value: String) {
		_uiState.update { it.copy(carat = value, caratError = null) }
	}

	fun onTaxRateChanged(value: String) {
		_uiState.update { it.copy(taxRate = value, taxRateError = null) }
	}

	fun createTaxPreference() {
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
				_effect.emit(CreateGoldTaxEffect.ShowMessage("Periksa kembali isian pajak"))
			}
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isSaving = true) }
			val validatedCarat = carat ?: return@launch
			val validatedTaxRate = taxRate ?: return@launch
			goldRepository.createTaxPreference(GoldTaxPreference(id = 0, carat = validatedCarat, taxRate = validatedTaxRate))
				.onSuccess {
					_uiState.update {
						it.copy(
							carat = "",
							taxRate = "",
							caratError = null,
							taxRateError = null,
							isSaving = false,
						)
					}
					_effect.emit(CreateGoldTaxEffect.ShowMessage("Preferensi pajak baru berhasil disimpan"))
					_effect.emit(CreateGoldTaxEffect.NavigateBack)
				}
				.onFailure { throwable ->
					_uiState.update { it.copy(isSaving = false) }
					_effect.emit(CreateGoldTaxEffect.ShowMessage(throwable.message ?: "Gagal menyimpan preferensi pajak"))
				}
		}
	}
}

sealed interface CreateGoldTaxEffect {
	data class ShowMessage(val message: String) : CreateGoldTaxEffect
	data object NavigateBack : CreateGoldTaxEffect
}
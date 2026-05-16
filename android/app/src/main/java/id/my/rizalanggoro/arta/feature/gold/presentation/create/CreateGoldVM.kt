package id.my.rizalanggoro.arta.feature.gold.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateGoldVM(
	private val goldRepository: GoldRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val goldRepository = (this[APPLICATION_KEY] as MyApplication).goldRepository
				CreateGoldVM(goldRepository = goldRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(CreateGoldUiState())
	val uiState: StateFlow<CreateGoldUiState> = _uiState.asStateFlow()

	private val _effect = MutableSharedFlow<CreateGoldEffect>()
	val effect: SharedFlow<CreateGoldEffect> = _effect.asSharedFlow()

	fun onWalletIdChanged(value: String) {
		_uiState.update { it.copy(walletId = value, walletIdError = null) }
	}

	fun onDateChanged(value: String) {
		_uiState.update { it.copy(date = value, dateError = null) }
	}

	fun onGramsChanged(value: String) {
		_uiState.update { it.copy(grams = value, gramsError = null) }
	}

	fun onPricePerGramChanged(value: String) {
		_uiState.update { it.copy(pricePerGram = value, pricePerGramError = null) }
	}

	fun onTypeChanged(value: String) {
		_uiState.update {
			it.copy(
				type = value,
				purityPercent = defaultPurityForType(value),
				purityPercentError = null,
			)
		}
	}

	fun onPurityPercentChanged(value: String) {
		_uiState.update { it.copy(purityPercent = value, purityPercentError = null) }
	}

	fun onNotesChanged(value: String) {
		_uiState.update { it.copy(notes = value) }
	}

	fun onWalletIdPrefilled(walletId: Int?) {
		if (walletId != null && _uiState.value.walletId.isBlank()) {
			_uiState.update { it.copy(walletId = walletId.toString()) }
		}
	}

	fun createGold() {
		val current = _uiState.value
		var hasError = false

		val walletId = current.walletId.toIntOrNull()
		if (walletId == null || walletId <= 0) {
			_uiState.update { it.copy(walletIdError = "Wallet ID wajib diisi") }
			hasError = true
		}

		if (current.date.isBlank() || runCatching { java.time.OffsetDateTime.parse(current.date) }.isFailure) {
			_uiState.update { it.copy(dateError = "Tanggal harus format ISO 8601") }
			hasError = true
		}

		val grams = current.grams.toDoubleOrNull()
		if (grams == null || grams <= 0) {
			_uiState.update { it.copy(gramsError = "Gram wajib berupa angka lebih dari 0") }
			hasError = true
		}

		val pricePerGram = current.pricePerGram.toDoubleOrNull()
		if (pricePerGram == null || pricePerGram <= 0) {
			_uiState.update { it.copy(pricePerGramError = "Harga per gram wajib berupa angka lebih dari 0") }
			hasError = true
		}

		val purityPercent = current.purityPercent.toDoubleOrNull()
		if (purityPercent == null || purityPercent < 0) {
			_uiState.update { it.copy(purityPercentError = "Persentase kemurnian tidak valid") }
			hasError = true
		}

		if (hasError) {
			viewModelScope.launch {
				_effect.emit(CreateGoldEffect.ShowMessage("Periksa kembali isian emas"))
			}
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			goldRepository.createGold(
				walletId = walletId!!,
				date = current.date,
				grams = grams!!,
				pricePerGram = pricePerGram!!,
				type = current.type,
				purityPercent = purityPercent!!,
				notes = current.notes,
			)
				.onSuccess { gold ->
					_effect.emit(CreateGoldEffect.ShowMessage("Data emas ${gold.type} berhasil dibuat"))
					_effect.emit(CreateGoldEffect.NavigateBack)
				}
				.onFailure { throwable ->
					_effect.emit(CreateGoldEffect.ShowMessage(throwable.message ?: "Gagal membuat data emas"))
				}
			_uiState.update { it.copy(isLoading = false) }
		}
	}

	private fun defaultPurityForType(type: String): String {
		return when (type) {
			"pure_gold" -> "99.9"
			"gold_jewelry" -> "75.0"
			"investment_gold" -> "99.9"
			else -> "0.0"
		}
	}
}

sealed interface CreateGoldEffect {
	data class ShowMessage(val message: String) : CreateGoldEffect
	data object NavigateBack : CreateGoldEffect
}

package id.my.rizalanggoro.arta.feature.gold.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
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
    private val selectedWalletPrefs: SelectedWalletPrefs,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                val goldRepository = app.goldRepository
                CreateGoldVM(
                    goldRepository = goldRepository,
                    selectedWalletPrefs = app.selectedWalletPrefs
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateGoldUiState())
    val uiState: StateFlow<CreateGoldUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<CreateGoldEffect>()
    val effect: SharedFlow<CreateGoldEffect> = _effect.asSharedFlow()

    fun onDateChanged(value: String) {
        _uiState.update { it.copy(date = value, dateError = null) }
    }

    fun onGramsChanged(value: String) {
        _uiState.update { it.copy(grams = value, gramsError = null) }
    }

    fun onPricePerGramChanged(value: String) {
        _uiState.update { it.copy(price = value, priceError = null) }
    }

    fun onTypeChanged(value: String) {
        _uiState.update {
            it.copy(
                type = value,
                carat = defaultCaratForType(value),
                caratError = null,
            )
        }
    }

    fun onCaratChanged(value: String) {
        _uiState.update { it.copy(carat = value, caratError = null) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun createGold() {
        val current = _uiState.value
        var hasError = false

        if (current.date.isBlank() || runCatching { java.time.OffsetDateTime.parse(current.date) }.isFailure) {
            _uiState.update { it.copy(dateError = "Tanggal harus format ISO 8601") }
            hasError = true
        }

        val grams = current.grams.toDoubleOrNull()
        if (grams == null || grams <= 0) {
            _uiState.update { it.copy(gramsError = "Gram wajib berupa angka lebih dari 0") }
            hasError = true
        }

        val price = current.price.toDoubleOrNull()
        if (price == null || price <= 0) {
            _uiState.update { it.copy(priceError = "Harga beli wajib berupa angka lebih dari 0") }
            hasError = true
        }

        val carat = current.carat.toDoubleOrNull()
        if (carat == null || carat <= 0 || carat > 24) {
            _uiState.update { it.copy(caratError = "Karat wajib berupa angka antara 0 dan 24") }
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
                walletId = requireNotNull(current.selectedWallet?.id) { "Wallet response missing id" },
                date = current.date,
                grams = grams!!,
                price = price!!,
                type = current.type,
                carat = carat!!,
                notes = current.notes,
            )
                .onSuccess { gold ->
                    _effect.emit(CreateGoldEffect.ShowMessage("Data emas ${gold.type} berhasil dibuat"))
                    _effect.emit(CreateGoldEffect.NavigateBack)
                }
                .onFailure { throwable ->
                    _effect.emit(
                        CreateGoldEffect.ShowMessage(
                            throwable.message ?: "Gagal membuat data emas"
                        )
                    )
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun defaultCaratForType(type: String): String {
        return when (type) {
            "pure_gold" -> "24.0"
            "jewelry" -> "18.0"
            else -> "0.0"
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

sealed interface CreateGoldEffect {
    data class ShowMessage(val message: String) : CreateGoldEffect
    data object NavigateBack : CreateGoldEffect
}

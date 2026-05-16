package id.my.rizalanggoro.arta.feature.gold.presentation.update

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

class UpdateGoldVM(
    private val goldRepository: GoldRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val goldRepository = (this[APPLICATION_KEY] as MyApplication).goldRepository
                UpdateGoldVM(goldRepository = goldRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(UpdateGoldUiState())
    val uiState: StateFlow<UpdateGoldUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpdateGoldEffect>()
    val effect: SharedFlow<UpdateGoldEffect> = _effect.asSharedFlow()

    fun load(goldId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            goldRepository.getGoldById(goldId)
                .onSuccess { gold ->
                    _uiState.update {
                        it.copy(
                            id = gold.id,
                            date = gold.date,
                            grams = gold.grams.toString(),
                            pricePerGram = gold.pricePerGram.toString(),
                            type = gold.type,
                            purityPercent = gold.purityPercent.toString(),
                            notes = gold.notes,
                        )
                    }
                }
                .onFailure { throwable ->
                    _effect.emit(UpdateGoldEffect.ShowMessage(throwable.message ?: "Gagal memuat data emas"))
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDateChanged(value: String) {
        _uiState.update { it.copy(date = value) }
    }

    fun onGramsChanged(value: String) {
        _uiState.update { it.copy(grams = value) }
    }

    fun onPricePerGramChanged(value: String) {
        _uiState.update { it.copy(pricePerGram = value) }
    }

    fun onTypeChanged(value: String) {
        _uiState.update { it.copy(type = value) }
    }

    fun onPurityPercentChanged(value: String) {
        _uiState.update { it.copy(purityPercent = value) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun updateGold() {
        val current = _uiState.value
        val id = current.id ?: run {
            viewModelScope.launch { _effect.emit(UpdateGoldEffect.ShowMessage("ID emas tidak ditemukan")) }
            return
        }

        val grams = current.grams.toDoubleOrNull()
        val pricePerGram = current.pricePerGram.toDoubleOrNull()
        val purity = current.purityPercent.toDoubleOrNull()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            goldRepository.updateGold(
                id = id,
                date = current.date.ifBlank { null },
                grams = grams,
                pricePerGram = pricePerGram,
                type = current.type.ifBlank { null },
                purityPercent = purity,
                notes = current.notes.ifBlank { null },
            ).onSuccess {
                _effect.emit(UpdateGoldEffect.NavigateBack)
            }.onFailure { throwable ->
                _effect.emit(UpdateGoldEffect.ShowMessage(throwable.message ?: "Gagal memperbarui data emas"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}

sealed interface UpdateGoldEffect {
    data class ShowMessage(val message: String) : UpdateGoldEffect
    object NavigateBack : UpdateGoldEffect
}

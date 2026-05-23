package id.my.rizalanggoro.arta.feature.gold.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.models.GoldUpdateGoldReq
import id.my.rizalanggoro.arta.domain.AuthSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateGoldVM(
    private val goldApi: GoldApi,
    private val authSessionProvider: () -> AuthSession?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                UpdateGoldVM(goldApi = app.goldApi, authSessionProvider = { app.authPrefs.currentSession.value })
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
            runCatching {
                val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.getGold("Bearer $token", goldId)
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                response.body() ?: throw IllegalStateException("Response body is null")
            }.onSuccess { res ->
                val gold = res.`data`
                _uiState.update {
                    it.copy(
                        id = gold.id,
                        date = gold.date,
                        grams = gold.grams.toString(),
                        price = gold.price.toString(),
                        type = gold.type,
                        carat = gold.carat.toString(),
                        notes = gold.notes,
                    )
                }
            }.onFailure { throwable ->
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

    fun onPriceChanged(value: String) {
        _uiState.update { it.copy(price = value) }
    }

    fun onTypeChanged(value: String) {
        _uiState.update { it.copy(type = value) }
    }

    fun onCaratChanged(value: String) {
        _uiState.update { it.copy(carat = value) }
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
        val price = current.price.toDoubleOrNull()
        val carat = current.carat.toDoubleOrNull()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val resp = goldApi.updateGold("Bearer $token", id, GoldUpdateGoldReq(
                    date = current.date.ifBlank { null },
                    grams = current.grams.toDoubleOrNull()?.let(java.math.BigDecimal::valueOf),
                    price = current.price.toDoubleOrNull()?.let(java.math.BigDecimal::valueOf),
                    type = current.type.ifBlank { null },
                    carat = current.carat.toDoubleOrNull()?.let(java.math.BigDecimal::valueOf),
                    notes = current.notes.ifBlank { null },
                ))
                if (!resp.isSuccessful) throw IllegalStateException(resp.errorBody()?.string() ?: "Request failed")
            }.onSuccess {
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

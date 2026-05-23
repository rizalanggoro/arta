package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoldDetailVM @Inject constructor(
    private val goldApi: GoldApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GoldDetailUiState())
    val uiState: StateFlow<GoldDetailUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GoldDetailEffect>()
    val effect: SharedFlow<GoldDetailEffect> = _effect.asSharedFlow()

    fun load(goldId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val token = authPrefs.currentSession.value?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.getGold("Bearer $token", goldId)
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                response.body() ?: throw IllegalStateException("Response body is null")
            }.onSuccess { res ->
                _uiState.update { it.copy(gold = res.`data`) }
            }.onFailure { throwable ->
                _effect.emit(GoldDetailEffect.ShowMessage(throwable.message ?: "Gagal memuat data emas"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDeleteRequested() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun confirmDelete() {
        val id = _uiState.value.gold?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val token = authPrefs.currentSession.value?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.deleteGold("Bearer $token", id)
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
            }.onSuccess {
                _effect.emit(GoldDetailEffect.NavigateBack)
            }.onFailure { throwable ->
                _effect.emit(GoldDetailEffect.ShowMessage(throwable.message ?: "Gagal menghapus data emas"))
            }
            _uiState.update { it.copy(isLoading = false, showDeleteDialog = false) }
        }
    }

    fun onEditClicked() {
        val id = _uiState.value.gold?.id ?: return
        viewModelScope.launch {
            _effect.emit(GoldDetailEffect.NavigateToEdit(id))
        }
    }
}

sealed interface GoldDetailEffect {
    data class ShowMessage(val message: String) : GoldDetailEffect
    data class NavigateToEdit(val goldId: Int) : GoldDetailEffect
    data object NavigateBack : GoldDetailEffect
}

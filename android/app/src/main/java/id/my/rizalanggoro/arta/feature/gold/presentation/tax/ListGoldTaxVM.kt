package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.openapi.models.DtoGoldTaxPreference
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.domain.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListGoldTaxVM(
    private val goldApi: GoldApi,
    private val authSessionProvider: () -> AuthSession?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                ListGoldTaxVM(goldApi = app.goldApi, authSessionProvider = { app.authPrefs.currentSession.value })
            }
        }
    }

    private val _uiState = MutableStateFlow(ListGoldTaxUiState())
    val uiState: StateFlow<ListGoldTaxUiState> = _uiState.asStateFlow()

    fun loadTaxPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                runCatching {
                    val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                    val response = goldApi.listGoldTaxPreferences("Bearer $token")
                    if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                    response.body() ?: throw IllegalStateException("Respons server kosong")
                }.onSuccess { res ->
                    val preferences = res.preferences
                    _uiState.update {
                        it.copy(
                            preferences = preferences,
                            deleteTarget = null,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat preferensi pajak",
                        )
                    }
                }
            }
        }
    }

    fun onDeleteRequested(preference: DtoGoldTaxPreference) {
        _uiState.update { it.copy(deleteTarget = preference) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(deleteTarget = null) }
    }

    fun confirmDeleteTaxPreference(preference: DtoGoldTaxPreference) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            viewModelScope.launch {
                runCatching {
                    val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                    val response = goldApi.deleteGoldTaxPreference("Bearer $token", preference.id)
                    if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                }.onSuccess {
                    _uiState.update { it.copy(isLoading = false, deleteTarget = null) }
                    loadTaxPreferences()
                }.onFailure { throwable ->
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

    init {
        loadTaxPreferences()
    }
}
package id.my.rizalanggoro.arta.feature.gold.presentation.tax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.models.DtoGoldTaxPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGoldTaxVM @Inject constructor(
    private val goldApi: GoldApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListGoldTaxUiState())
    val uiState: StateFlow<ListGoldTaxUiState> = _uiState.asStateFlow()

    fun loadTaxPreferences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val token = authPrefs.currentSession.value?.token
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.listGoldTaxPreferences("Bearer $token")
                if (!response.isSuccessful) throw IllegalStateException(
                    response.errorBody()?.string() ?: "Request failed"
                )
                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { res ->
                _uiState.update {
                    it.copy(
                        preferences = res.preferences,
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

    fun onDeleteClicked(preference: DtoGoldTaxPreference) = _uiState.update {
        it.copy(
            deleteTarget = preference
        )
    }

    fun onDialogDismissed() = _uiState.update {
        it.copy(
            deleteTarget = null
        )
    }

    fun onDeleteClicked() {
        val preference = _uiState.value.deleteTarget ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDeleting = true,
                    errorMessage = null
                )
            }

            runCatching {
                val token = authPrefs.currentSession.value?.token
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.deleteGoldTaxPreference("Bearer $token", preference.id)
                if (!response.isSuccessful) throw IllegalStateException(
                    response.errorBody()?.string() ?: "Request failed"
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteTarget = null
                    )
                }
                loadTaxPreferences()
                AppEventBus.emit(AppEvent.GoldTaxChanged)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isDeleting = false,

                        errorMessage = throwable.message ?: "Gagal menghapus preferensi pajak",
                    )
                }
            }
        }
    }

    init {
        loadTaxPreferences()
    }
}

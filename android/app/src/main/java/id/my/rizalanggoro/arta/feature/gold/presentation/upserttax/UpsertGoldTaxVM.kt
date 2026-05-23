package id.my.rizalanggoro.arta.feature.gold.presentation.upserttax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.models.GoldGoldTaxPreferenceReq
import id.my.rizalanggoro.arta.domain.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertGoldTaxVM(
    private val taxPreferenceId: Int,
    private val goldApi: GoldApi,
    private val authSessionProvider: () -> AuthSession?,
) : ViewModel() {
    companion object {
        fun Factory(taxPreferenceId: Int) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                UpsertGoldTaxVM(
                    taxPreferenceId = taxPreferenceId,
                    goldApi = app.goldApi,
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(
        UpsertGoldTaxUiState(isUpdate = taxPreferenceId != 0),
    )
    val uiState = _uiState.asStateFlow()

    fun load() {
        if (taxPreferenceId == 0) {
            _uiState.update { it.copy(isLoading = false, isUpdate = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.listGoldTaxPreferences("Bearer $token")
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { res ->
                    val preferences = res.preferences
                    val preference = preferences.firstOrNull { it.id == taxPreferenceId }
                    if (preference == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Preferensi pajak tidak ditemukan",
                            )
                        }
                        return@launch
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isUpdate = true,
                            carat = preference.carat.toString(),
                            taxRate = preference.taxRate.toString(),
                            caratError = null,
                            taxRateError = null,
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

    fun onCaratChanged(carat: String) {
        _uiState.update {
            it.copy(
                carat = carat,
                caratError = null,
                errorMessage = null,
            )
        }
    }

    fun onTaxRateChanged(taxRate: String) {
        _uiState.update {
            it.copy(
                taxRate = taxRate,
                taxRateError = null,
                errorMessage = null,
            )
        }
    }

    fun onSubmitClicked() {
        val current = _uiState.value
        val carat = current.carat.trim().toDoubleOrNull()
        val taxRate = current.taxRate.trim().toDoubleOrNull()
        val caratError = when {
            carat == null || carat <= 0 || carat > 24 -> "Karat harus antara 0 dan 24"
            else -> null
        }
        val taxRateError = when {
            taxRate == null || taxRate < 0 || taxRate > 100 -> "Rasio pajak harus antara 0 dan 100"
            else -> null
        }

        if (caratError != null || taxRateError != null) {
            _uiState.update {
                it.copy(
                    caratError = caratError,
                    taxRateError = taxRateError,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when {
                current.isUpdate -> {
                    runCatching {
                        val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                        val resp = goldApi.updateGoldTaxPreference("Bearer $token", taxPreferenceId, GoldGoldTaxPreferenceReq(carat = java.math.BigDecimal.valueOf(carat!!), taxRate = java.math.BigDecimal.valueOf(taxRate!!)))
                        if (!resp.isSuccessful) throw IllegalStateException(resp.errorBody()?.string() ?: "Request failed")
                    }
                }

                else -> {
                    runCatching {
                        val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                        val resp = goldApi.createGoldTaxPreference("Bearer $token", GoldGoldTaxPreferenceReq(carat = java.math.BigDecimal.valueOf(carat!!), taxRate = java.math.BigDecimal.valueOf(taxRate!!)))
                        if (!resp.isSuccessful) throw IllegalStateException(resp.errorBody()?.string() ?: "Request failed")
                    }
                }
            }.onSuccess {
                AppEventBus.emit(AppEvent.GoldTaxChanged)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal menyimpan preferensi pajak",
                    )
                }
            }
        }
    }

    init {
        load()
    }
}
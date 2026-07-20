package id.my.rizalanggoro.arta.feature.gold.presentation.upserttax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.models.GoldGoldTaxPreferenceReq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpsertGoldTaxVM @Inject constructor(
    private val goldApi: GoldApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private var taxPreferenceId: Int = 0

    private val _uiState = MutableStateFlow(UpsertGoldTaxUiState(isUpdate = false))
    val uiState = _uiState.asStateFlow()

    fun setTaxPreferenceId(value: Int) {
        if (taxPreferenceId == value) return
        taxPreferenceId = value
        _uiState.update { it.copy(isUpdate = value != 0) }
        load()
    }

    fun load() {
        if (taxPreferenceId == 0) {
            _uiState.update { it.copy(isLoading = false, isUpdate = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val token = authPrefs.currentSession.value?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.listGoldTaxPreferences("Bearer $token")
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { res ->
                val preference = res.preferences.firstOrNull { it.id == taxPreferenceId }
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
            taxRate == null -> "Rasio pajak harus diisi"
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
                current.isUpdate -> runCatching {
                    val token = authPrefs.currentSession.value?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                    val response = goldApi.updateGoldTaxPreference(
                        "Bearer $token",
                        taxPreferenceId,
                        GoldGoldTaxPreferenceReq(
                            carat = carat!!,
                            taxRate = taxRate!!,
                        ),
                    )
                    if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                }

                else -> runCatching {
                    val token = authPrefs.currentSession.value?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                    val response = goldApi.createGoldTaxPreference(
                        "Bearer $token",
                        GoldGoldTaxPreferenceReq(
                            carat = carat!!,
                            taxRate = taxRate!!,
                        ),
                    )
                    if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
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

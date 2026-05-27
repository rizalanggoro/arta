package id.my.rizalanggoro.arta.feature.gold.presentation.upsert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.models.GoldCreateGoldReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpsertGoldVM @Inject constructor(
    private val goldApi: GoldApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private var goldId: Int = 0

    private val _uiState = MutableStateFlow(UpsertGoldUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<UpsertGoldUiState.Event>()
    val event = _event.asSharedFlow()

    fun setGoldId(value: Int) {
        if (goldId == value) return
        goldId = value
//        if (value != 0) {
//            loadGold(value)
//        }
    }

    fun onSelectDateClicked() = _uiState.update {
        it.copy(isDatePickerOpen = true)
    }

    fun onDateChanged(date: Long?) = _uiState.update {
        if (date == null) return@update it
        it.copy(
            date = date,
            isDatePickerOpen = false
        )
    }

    fun onDatePickerDismissed() = _uiState.update {
        it.copy(isDatePickerOpen = false)
    }

    fun onGramsChanged(value: String) {
        _uiState.update { it.copy(grams = value, gramsError = null) }
    }

    fun onPriceChanged(value: String) {
        _uiState.update { it.copy(price = value, priceError = null) }
    }

    fun onTypeChanged(value: String) = _uiState.update {
        it.copy(type = value)
    }

    fun onCaratChanged(value: String) {
        _uiState.update { it.copy(carat = value, caratError = null) }
    }

    fun onNotesChanged(value: String) {
        _uiState.update { it.copy(notes = value) }
    }

    fun onSubmitClicked() {
        val current = _uiState.value
        var hasError = false

        val grams = current.grams.toDoubleOrNull()
        if (grams == null || grams <= 0) {
            _uiState.update { it.copy(gramsError = "Gram wajib berupa angka lebih dari 0") }
            hasError = true
        }

        val price = current.price.toIntOrNull()
        if (price == null || price <= 0) {
            _uiState.update { it.copy(priceError = "Harga beli wajib berupa angka lebih dari 0") }
            hasError = true
        }

        val carat = current.carat.toDoubleOrNull()
        if (carat == null || carat <= 0 || carat > 24) {
            _uiState.update { it.copy(caratError = "Karat wajib berupa angka antara 0 dan 24") }
            hasError = true
        }

        val walletId = current.selectedWallet?.id
        if (walletId == null && !current.isUpdate) {
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

//                if (current.isUpdate) {
//                    val response = goldApi.updateGold(
//                        authorization = authorization,
//                        id = current.goldId,
//                        body = GoldUpdateGoldReq(
//                            date = current.date.ifBlank { null },
//                            grams = BigDecimal.valueOf(grams!!),
//                            price = BigDecimal.valueOf(price!!),
//                            type = current.type.ifBlank { null },
//                            carat = BigDecimal.valueOf(carat!!),
//                            notes = current.notes.ifBlank { null },
//                        ),
//                    )
//
//                    if (!response.isSuccessful) {
//                        throw IllegalStateException(response.errorMessage())
//                    }
//
//                    response.body() ?: throw IllegalStateException("Respons server kosong")
//                } else {

                val response = goldApi.createGold(
                    authorization = authorization,
                    body = GoldCreateGoldReq(
                        walletId = requireNotNull(walletId) { "Dompet aktif tidak ditemukan" },
                        date = current.date.toApiFormat(),
                        grams = grams!!,
                        price = price!!,
                        type = current.type,
                        carat = carat!!,
                        notes = current.notes,
                    ),
                )

                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
//                }
            }.onSuccess {
                AppEventBus.emit(AppEvent.GoldChanged)
            }.onFailure { throwable ->
                _event.emit(
                    UpsertGoldUiState.Event.ShowMessage(
                        throwable.message ?: "Terjadi kesalahan tak terduga"
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

//    private fun loadGold(id: Int) {
//        viewModelScope.launch {
//            _uiState.update { it.copy(goldId = id, isUpdate = true, isLoading = true) }
//            runCatching {
//                val authorization = authorizationHeader()
//                    ?: throw IllegalStateException("Sesi login tidak ditemukan")
//
//                val response = goldApi.getGold(authorization, id)
//                if (!response.isSuccessful) {
//                    throw IllegalStateException(response.errorMessage())
//                }
//
//                response.body() ?: throw IllegalStateException("Respons server kosong")
//            }.onSuccess { response ->
//                val gold = response.data
//                _uiState.update {
//                    it.copy(
//                        goldId = gold.id,
//                        isUpdate = true,
//                        date = gold.date,
//                        grams = gold.grams.toString(),
//                        price = gold.price.toString(),
//                        type = gold.type,
//                        carat = gold.carat.toString(),
//                        notes = gold.notes,
//                        gramsError = null,
//                        priceError = null,
//                        caratError = null,
//                        isLoading = false,
//                    )
//                }
//            }.onFailure { throwable ->
//                _uiState.update { it.copy(isLoading = false) }
//                _effect.emit(
//                    UpsertGoldEffect.ShowMessage(
//                        throwable.message ?: "Gagal memuat data emas",
//                    )
//                )
//            }
//        }
//    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    init {
        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update { it.copy(selectedWallet = wallet) }
            }
        }
    }
}
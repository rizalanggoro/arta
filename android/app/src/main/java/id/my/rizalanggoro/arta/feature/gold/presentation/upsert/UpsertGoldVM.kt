package id.my.rizalanggoro.arta.feature.gold.presentation.upsert

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.application.route.GoldRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.core.extension.toMillis
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.models.GoldCreateGoldReq
import id.my.rizalanggoro.arta.openapi.models.GoldUpdateGoldReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel(assistedFactory = UpsertGoldVM.Factory::class)
class UpsertGoldVM @AssistedInject constructor(
    @param:ApplicationContext private val context: Context,
    @Assisted private val navKey: GoldRoute.Upsert,
    private val goldApi: GoldApi,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: GoldRoute.Upsert): UpsertGoldVM
    }

    private val _uiState = MutableStateFlow(UpsertGoldUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<UpsertGoldUiState.Event>()
    val event = _event.asSharedFlow()

    // Reused across retries so the server can dedupe; rotated only when the
    // server definitively rejected a submission (see onFailure in onSubmitClicked).
    private var idempotencyKey: String = UUID.randomUUID().toString()

    fun onSelectDateClicked() = _uiState.update {
        it.copy(isDatePickerOpen = true)
    }

    fun onDateChanged(date: Long?) = _uiState.update {
        if (date == null) return@update it
        it.copy(date = date, isDatePickerOpen = false)
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
        val walletId = current.selectedWallet?.id
        val grams = current.grams.toDoubleOrNull()
        val price = current.price.toDoubleOrNull()
        val carat = current.carat.toDoubleOrNull()
        var hasError = false

        if (grams == null || grams <= 0) {
            _uiState.update { it.copy(gramsError = "Gram wajib berupa angka lebih dari 0") }
            hasError = true
        }

        if (price == null || price <= 0) {
            _uiState.update { it.copy(priceError = "Harga beli wajib berupa angka lebih dari 0") }
            hasError = true
        }

        if (carat == null || carat <= 0 || carat > 24) {
            _uiState.update { it.copy(caratError = "Karat wajib berupa angka antara 0 dan 24") }
            hasError = true
        }

        if (walletId == null && !current.isUpdate) {
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching {
                if (current.isUpdate) {
                    val response = goldApi.updateGold(
                        authorization = authPrefs.authorization(),
                        id = navKey.goldId,
                        body = GoldUpdateGoldReq(
                            date = current.date.toApiFormat(),
                            grams = grams!!,
                            price = price!!,
                            type = current.type,
                            carat = carat!!,
                            notes = current.notes.ifBlank { null },
                        ),
                    )

                    if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

                    response.body() ?: throw IllegalStateException(
                        context.getString(R.string.server_empty_error)
                    )
                } else {
                    val response = goldApi.createGold(
                        authorization = authPrefs.authorization(),
                        body = GoldCreateGoldReq(
                            walletId = requireNotNull(walletId) { "Dompet aktif tidak ditemukan" },
                            date = current.date.toApiFormat(),
                            grams = grams!!,
                            price = price!!,
                            type = current.type,
                            carat = carat!!,
                            notes = current.notes,
                        ),
                        idempotencyKey = idempotencyKey,
                    )

                    if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

                    response.body() ?: throw IllegalStateException(
                        context.getString(R.string.server_empty_error)
                    )
                }
            }.onSuccess {
                AppEventBus.emit(AppEvent.GoldChanged)
            }.onFailure { throwable ->
                if (throwable is IllegalStateException) {
                    idempotencyKey = UUID.randomUUID().toString()
                }
                _event.emit(
                    UpsertGoldUiState.Event.ShowMessage(
                        throwable.message ?: context.getString(R.string.client_error)
                    )
                )
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadGold() = viewModelScope.launch {
        _uiState.update {
            it.copy(
                goldId = navKey.goldId,
                isUpdate = true,
                isLoading = true,
            )
        }
        runCatching {
            val response = goldApi.getGold(
                authorization = authPrefs.authorization(),
                id = navKey.goldId,
            )

            if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

            response.body() ?: throw IllegalStateException(
                context.getString(R.string.server_empty_error)
            )
        }.onSuccess { body ->
            val gold = body.`data`
            _uiState.update {
                it.copy(
                    isLoading = false,
                    date = gold.date.toMillis(),
                    grams = gold.grams.formatNumber(),
                    price = gold.price.formatNumber(),
                    type = gold.type,
                    carat = gold.carat.formatNumber(),
                    notes = gold.notes,
                )
            }
        }.onFailure { throwable ->
            _uiState.update { it.copy(isLoading = false) }
            _event.emit(
                UpsertGoldUiState.Event.ShowMessage(
                    throwable.message ?: context.getString(R.string.client_error)
                )
            )
        }
    }

    init {
        if (navKey.goldId > 0)
            loadGold()

        viewModelScope.launch {
            selectedWalletPrefs.selectedWallet.collect { wallet ->
                _uiState.update { it.copy(selectedWallet = wallet) }
            }
        }
    }
}

private fun Double.formatNumber(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        this.toString()
    }
}

package id.my.rizalanggoro.arta.feature.wallet.presentation.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListWalletVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val walletApi: WalletApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListWalletUiState())
    val uiState: StateFlow<ListWalletUiState> = _uiState.asStateFlow()

    fun loadWallets() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            runCatching {
                val response = walletApi.listWallets(
                    authorization = authPrefs.authorization()
                )

                if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

                response.body() ?: throw IllegalStateException(
                    context.getString(
                        R.string.server_empty_error
                    )
                )
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        wallets = response.wallets,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: context.getString(
                            R.string.client_error
                        ),
                    )
                }
            }
        }
    }

    init {
        loadWallets()

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.WalletChanged>()
                .collect { loadWallets() }
        }
    }
}

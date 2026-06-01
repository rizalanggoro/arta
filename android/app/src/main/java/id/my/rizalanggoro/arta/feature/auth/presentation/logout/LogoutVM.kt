package id.my.rizalanggoro.arta.feature.auth.presentation.logout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.AuthApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authPrefs: AuthPrefs,
    private val selectedWalletPrefs: SelectedWalletPrefs,
    private val authApi: AuthApi
) : ViewModel() {
    private var _uiState = MutableStateFlow(LogoutUiState())
    val uiState = _uiState.asStateFlow()

    private var _event = MutableSharedFlow<LogoutUiState.Event>()
    val event = _event.asSharedFlow()

    fun logout() = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }

        runCatching {
            val response = authApi.logout(
                authorization = authPrefs.authorization(),
            )

            if (response.isSuccessful.not()) throw IllegalStateException(response.errorMessage())

            response.body() ?: throw IllegalStateException(
                context.getString(R.string.server_empty_error)
            )
        }.onSuccess {
            authPrefs.clear()
            selectedWalletPrefs.clear()

            _event.emit(LogoutUiState.Event.LogoutSucceeded)
        }.onFailure {
        }.also {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}
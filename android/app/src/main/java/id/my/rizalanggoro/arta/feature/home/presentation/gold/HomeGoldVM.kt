package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.domain.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeGoldVM(private val goldApi: GoldApi, private val authSessionProvider: () -> AuthSession?) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                HomeGoldVM(app.goldApi, { app.authPrefs.currentSession.value })
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeGoldUiState())
    val uiState: StateFlow<HomeGoldUiState> = _uiState.asStateFlow()

    private fun fetchGolds() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                val token = authSessionProvider()?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
                val response = goldApi.listGolds("Bearer $token")
                if (!response.isSuccessful) throw IllegalStateException(response.errorBody()?.string() ?: "Request failed")
                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { res ->
                val list = res.golds.map { it.`data` }
                _uiState.value = _uiState.value.copy(isLoading = false, golds = list)
            }.onFailure { thr ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = thr.message)
            }
        }
    }

    init {
        fetchGolds()
    }
}

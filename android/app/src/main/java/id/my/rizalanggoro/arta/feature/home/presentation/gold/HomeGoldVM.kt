package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeGoldVM @Inject constructor(
    private val goldApi: GoldApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeGoldUiState())
    val uiState: StateFlow<HomeGoldUiState> = _uiState.asStateFlow()

    private fun fetchGolds() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                val token = authPrefs.currentSession.value?.token ?: throw IllegalStateException("Sesi login tidak ditemukan")
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

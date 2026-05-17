package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoldListVM(private val goldRepository: GoldRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                val goldRepo = GoldRepository(
                    apiService = RetrofitProvider.create(id.my.rizalanggoro.arta.feature.gold.data.GoldApiService::class.java),
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
                GoldListVM(goldRepo)
            }
        }
    }
    private val _uiState = MutableStateFlow(GoldListUiState())
    val uiState: StateFlow<GoldListUiState> = _uiState.asStateFlow()

    init {
        fetchGolds()
    }

    private fun fetchGolds() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = goldRepository.listGolds()
            if (res.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, golds = res.getOrNull() ?: emptyList())
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.exceptionOrNull()?.message)
            }
        }
    }
}

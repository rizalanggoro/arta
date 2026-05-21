package id.my.rizalanggoro.arta.feature.home.presentation.gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeGoldVM(private val goldRepository: GoldRepository) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                val goldRepo = GoldRepository(
                    apiService = RetrofitProvider.create(id.my.rizalanggoro.arta.feature.gold.data.GoldApiService::class.java),
                    authSessionProvider = { app.authPrefs.currentSession.value },
                )
                HomeGoldVM(goldRepo)
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeGoldUiState())
    val uiState: StateFlow<HomeGoldUiState> = _uiState.asStateFlow()

    private fun fetchGolds() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val res = goldRepository.listGolds()
            if (res.isSuccess) {
                _uiState.value =
                    _uiState.value.copy(isLoading = false, golds = res.getOrNull() ?: emptyList())
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = res.exceptionOrNull()?.message
                )
            }
        }
    }

    init {
        fetchGolds()
    }
}

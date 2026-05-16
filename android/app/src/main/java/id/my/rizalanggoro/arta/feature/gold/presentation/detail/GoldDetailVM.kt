package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GoldDetailVM(
    private val goldRepository: GoldRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val goldRepository = (this[APPLICATION_KEY] as MyApplication).goldRepository
                GoldDetailVM(goldRepository = goldRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(GoldDetailUiState())
    val uiState: StateFlow<GoldDetailUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<GoldDetailEffect>()
    val effect: SharedFlow<GoldDetailEffect> = _effect.asSharedFlow()

    fun load(goldId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            goldRepository.getGoldById(goldId)
                .onSuccess { gold ->
                    _uiState.update { it.copy(gold = gold) }
                }
                .onFailure { throwable ->
                    _effect.emit(GoldDetailEffect.ShowMessage(throwable.message ?: "Gagal memuat data emas"))
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDeleteRequested() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun confirmDelete() {
        val id = _uiState.value.gold?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            goldRepository.deleteGold(id)
                .onSuccess {
                    _effect.emit(GoldDetailEffect.NavigateBack)
                }
                .onFailure { throwable ->
                    _effect.emit(GoldDetailEffect.ShowMessage(throwable.message ?: "Gagal menghapus data emas"))
                }
            _uiState.update { it.copy(isLoading = false, showDeleteDialog = false) }
        }
    }

    fun onEditClicked() {
        val id = _uiState.value.gold?.id ?: return
        viewModelScope.launch {
            _effect.emit(GoldDetailEffect.NavigateToEdit(id))
        }
    }
}

sealed interface GoldDetailEffect {
    data class ShowMessage(val message: String) : GoldDetailEffect
    data class NavigateToEdit(val goldId: Int) : GoldDetailEffect
    object NavigateBack : GoldDetailEffect
}

package id.my.rizalanggoro.arta.feature.category.presentation.upsert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.category.data.CategoryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpsertCategoryVM(
    private val categoryId: Int,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    companion object {
        fun Factory(categoryId: Int) = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                UpsertCategoryVM(
                    categoryId = categoryId,
                    categoryRepository = app.categoryRepository,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(
        UpsertCategoryUiState(isUpdate = categoryId != 0)
    )
    val uiState: StateFlow<UpsertCategoryUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<UpsertCategoryEffect>()
    val effect: SharedFlow<UpsertCategoryEffect> = _effect.asSharedFlow()

    fun loadCategory() {
        if (categoryId == 0) {
            _uiState.update { it.copy(isLoading = false, isUpdate = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            categoryRepository.getCategoryById(categoryId)
                .onSuccess { category ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isUpdate = true,
                            name = category.name,
                            type = category.type,
                            nameError = null,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat kategori",
                        )
                    }
                }
        }
    }

    fun onChangeName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null, errorMessage = null) }
    }

    fun onChangeType(value: String) {
        _uiState.update { it.copy(type = value, errorMessage = null) }
    }

    fun submit() {
        val current = _uiState.value
        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama kategori wajib diisi") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (current.isUpdate) {
                categoryRepository.updateCategory(
                    id = categoryId,
                    name = current.name,
                    type = current.type,
                )
            } else {
                categoryRepository.createCategory(
                    name = current.name,
                    type = current.type,
                )
            }

            result
                .onSuccess {
                    _effect.emit(UpsertCategoryEffect.NavigateBack)
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal menyimpan kategori",
                        )
                    }
                }
        }
    }
}

sealed interface UpsertCategoryEffect {
    data object NavigateBack : UpsertCategoryEffect
}
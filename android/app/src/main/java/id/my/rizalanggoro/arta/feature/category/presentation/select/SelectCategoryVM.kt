package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.category.data.CategoryRepository
import id.my.rizalanggoro.arta.domain.Category
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectCategoryVM(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                SelectCategoryVM(categoryRepository = app.categoryRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(SelectCategoryUiState())
    val uiState: StateFlow<SelectCategoryUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<SelectCategoryEffect>()
    val effect: SharedFlow<SelectCategoryEffect> = _effect.asSharedFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories, isLoading = false) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message ?: "Gagal memuat kategori") }
                }
        }
    }

    fun selectCategory(category: Category) {
        viewModelScope.launch {
            CategorySelectionBus.emit(category)
            _effect.emit(SelectCategoryEffect.NavigateBack)
        }
    }
}

sealed interface SelectCategoryEffect {
    data object NavigateBack : SelectCategoryEffect
}
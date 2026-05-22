package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.domain.Category
import id.my.rizalanggoro.arta.feature.category.data.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            categoryRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories, isLoading = false) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat kategori"
                        )
                    }
                }
        }
    }

    fun onCategoryTypeSelected(type: String) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun selectCategory(category: Category) {
        viewModelScope.launch {
            AppEventBus.emit(AppEvent.CategorySelected(category = category))
        }
    }

    init {
        loadCategories()
    }
}

sealed interface SelectCategoryEffect {
    data object NavigateBack : SelectCategoryEffect
}
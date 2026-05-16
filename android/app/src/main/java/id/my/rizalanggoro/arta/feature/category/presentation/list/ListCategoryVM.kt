package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.feature.category.data.CategoryRepository
import id.my.rizalanggoro.arta.domain.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListCategoryVM(
	private val categoryRepository: CategoryRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val categoryRepository = (this[APPLICATION_KEY] as MyApplication).categoryRepository
				ListCategoryVM(categoryRepository = categoryRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(ListCategoryUiState())
	val uiState: StateFlow<ListCategoryUiState> = _uiState.asStateFlow()

	fun loadCategories() {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			categoryRepository.getCategories()
				.onSuccess { categories ->
					_uiState.update {
						it.copy(
							categories = categories,
							isLoading = false,
							errorMessage = null,
							deleteTarget = null,
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

	fun onDeleteRequested(category: Category) {
		_uiState.update { it.copy(deleteTarget = category) }
	}

	fun dismissDeleteDialog() {
		_uiState.update { it.copy(deleteTarget = null) }
	}

	fun confirmDeleteCategory(category: Category) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			categoryRepository.deleteCategory(category.id)
				.onSuccess {
					_uiState.update { it.copy(isLoading = false, deleteTarget = null) }
					loadCategories()
				}
				.onFailure { throwable ->
					_uiState.update {
						it.copy(
							isLoading = false,
							errorMessage = throwable.message ?: "Gagal menghapus kategori",
						)
					}
				}
		}
	}
}
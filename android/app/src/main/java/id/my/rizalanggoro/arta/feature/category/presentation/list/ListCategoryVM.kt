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

	fun loadCategories(type: String = _uiState.value.selectedType) {
		viewModelScope.launch {
			val selectedType = type.ifBlank { "expense" }
			_uiState.update { it.copy(isLoading = true, errorMessage = null, selectedType = selectedType) }
			categoryRepository.getCategories(type = selectedType)
				.onSuccess { categories ->
					_uiState.update {
						it.copy(
							categories = categories,
							isLoading = false,
							errorMessage = null,
							actionTarget = null,
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

	fun onCategoryTypeSelected(type: String) {
		if (_uiState.value.selectedType == type) {
			return
		}
		loadCategories(type)
	}

	fun onCategoryClicked(category: Category) {
		if (category.userId == null) {
			return
		}
		_uiState.update { it.copy(actionTarget = category, deleteTarget = null) }
	}

	fun dismissActionSheet() {
		_uiState.update { it.copy(actionTarget = null) }
	}

	fun onDeleteRequested(category: Category) {
		_uiState.update { it.copy(deleteTarget = category, actionTarget = null) }
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
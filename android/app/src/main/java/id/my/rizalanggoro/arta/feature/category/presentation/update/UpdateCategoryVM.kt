package id.my.rizalanggoro.arta.feature.category.presentation.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
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

class UpdateCategoryVM(
	private val categoryRepository: CategoryRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val categoryRepository = (this[APPLICATION_KEY] as MyApplication).categoryRepository
				UpdateCategoryVM(categoryRepository = categoryRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(UpdateCategoryUiState())
	val uiState: StateFlow<UpdateCategoryUiState> = _uiState.asStateFlow()

	private val _effect = MutableSharedFlow<UpdateCategoryEffect>()
	val effect: SharedFlow<UpdateCategoryEffect> = _effect.asSharedFlow()

	fun loadCategory(categoryId: Int) {
		viewModelScope.launch {
			_uiState.update { it.copy(categoryId = categoryId, isLoading = true, errorMessage = null) }
			categoryRepository.getCategoryById(categoryId)
				.onSuccess { category ->
					_uiState.update {
						it.copy(
							categoryId = category.id,
							name = category.name,
							type = category.type,
							isLoading = false,
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
		_uiState.update { it.copy(name = value, nameError = null) }
	}

	fun onChangeType(value: String) {
		_uiState.update { it.copy(type = value, nameError = null) }
	}

	fun updateCategory() {
		val current = _uiState.value
		val categoryId = current.categoryId

		if (categoryId == null) {
			viewModelScope.launch {
				_effect.emit(UpdateCategoryEffect.ShowMessage("Kategori belum dimuat"))
			}
			return
		}

		if (current.name.isBlank()) {
			_uiState.update { it.copy(nameError = "Nama kategori wajib diisi") }
			viewModelScope.launch {
				_effect.emit(UpdateCategoryEffect.ShowMessage("Periksa kembali isian kategori"))
			}
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			categoryRepository.updateCategory(
				id = categoryId,
				name = current.name,
				type = current.type,
			)
				.onSuccess { category ->
					_effect.emit(UpdateCategoryEffect.ShowMessage("Kategori ${category.name} berhasil diperbarui"))
					_effect.emit(UpdateCategoryEffect.NavigateBack)
				}
				.onFailure { throwable ->
					_effect.emit(UpdateCategoryEffect.ShowMessage(throwable.message ?: "Gagal memperbarui kategori"))
				}
			_uiState.update { it.copy(isLoading = false) }
		}
	}

}

sealed interface UpdateCategoryEffect {
	data class ShowMessage(val message: String) : UpdateCategoryEffect
	data object NavigateBack : UpdateCategoryEffect
}
package id.my.rizalanggoro.arta.feature.category.presentation.create

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

class CreateCategoryVM(
	private val categoryRepository: CategoryRepository,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val categoryRepository = (this[APPLICATION_KEY] as MyApplication).categoryRepository
				CreateCategoryVM(categoryRepository = categoryRepository)
			}
		}
	}

	private val _uiState = MutableStateFlow(CreateCategoryUiState())
	val uiState: StateFlow<CreateCategoryUiState> = _uiState.asStateFlow()

	private val _effect = MutableSharedFlow<CreateCategoryEffect>()
	val effect: SharedFlow<CreateCategoryEffect> = _effect.asSharedFlow()

	fun onChangeName(value: String) {
		_uiState.update { it.copy(name = value, nameError = null) }
	}

	fun onChangeType(value: String) {
		_uiState.update {
			it.copy(
				type = value,
				icon = defaultIconForType(value),
				color = defaultColorForType(value),
				nameError = null,
			)
		}
	}

	fun onChangeIcon(value: String) {
		_uiState.update { it.copy(icon = value) }
	}

	fun onChangeColor(value: String) {
		_uiState.update { it.copy(color = value) }
	}

	fun createCategory() {
		val current = _uiState.value

		if (current.name.isBlank()) {
			_uiState.update { it.copy(nameError = "Nama kategori wajib diisi") }
			viewModelScope.launch {
				_effect.emit(CreateCategoryEffect.ShowMessage("Periksa kembali isian kategori"))
			}
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true) }
			categoryRepository.createCategory(
				name = current.name,
				type = current.type,
				icon = current.icon.ifBlank { defaultIconForType(current.type) },
				color = current.color.ifBlank { defaultColorForType(current.type) },
			)
				.onSuccess { category ->
					_effect.emit(CreateCategoryEffect.ShowMessage("Kategori ${category.name} berhasil dibuat"))
					_effect.emit(CreateCategoryEffect.NavigateBack)
				}
				.onFailure { throwable ->
					_effect.emit(CreateCategoryEffect.ShowMessage(throwable.message ?: "Gagal membuat kategori"))
				}
			_uiState.update { it.copy(isLoading = false) }
		}
	}

	private fun defaultIconForType(type: String): String {
		return when (type) {
			"income" -> "💰"
			"general" -> "🏷️"
			else -> "🧾"
		}
	}

	private fun defaultColorForType(type: String): String {
		return when (type) {
			"income" -> "#16A34A"
			"general" -> "#2563EB"
			else -> "#E11D48"
		}
	}
}

sealed interface CreateCategoryEffect {
	data class ShowMessage(val message: String) : CreateCategoryEffect
	data object NavigateBack : CreateCategoryEffect
}
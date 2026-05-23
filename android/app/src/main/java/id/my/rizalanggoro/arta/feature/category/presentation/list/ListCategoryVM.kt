package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import id.my.rizalanggoro.arta.core.application.MyApplication
import kotlinx.serialization.json.Json
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListCategoryVM(
	private val categoryApi: CategoryApi,
	private val authSessionProvider: () -> String?,
) : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				val app = (this[APPLICATION_KEY] as MyApplication)
				ListCategoryVM(
					categoryApi = app.categoryApi,
					authSessionProvider = { app.authPrefs.currentSession.value?.token },
				)
			}
		}
	}

	private val _uiState = MutableStateFlow(ListCategoryUiState())
	val uiState: StateFlow<ListCategoryUiState> = _uiState.asStateFlow()

	fun loadCategories(type: String = _uiState.value.selectedType) {
		viewModelScope.launch {
			val selectedType = type.ifBlank { "expense" }
			_uiState.update { it.copy(isLoading = true, errorMessage = null, selectedType = selectedType) }
			runCatching {
				val authorization = authorizationHeader()
					?: throw IllegalStateException("Sesi login tidak ditemukan")

				val response = categoryApi.listCategories(authorization)
				if (!response.isSuccessful) {
					val j = Json { ignoreUnknownKeys = true }
					throw IllegalStateException(response.errorMessage(j))
				}

				response.body() ?: throw IllegalStateException("Respons server kosong")
			}.onSuccess { response ->
				_uiState.update {
					it.copy(
						categories = response.categories,
						isLoading = false,
						errorMessage = null,
						actionTarget = null,
						deleteTarget = null,
					)
				}
			}.onFailure { throwable ->
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

	fun onCategoryClicked(category: DomainCategory) {
		if (category.userId == null) {
			return
		}
		_uiState.update { it.copy(actionTarget = category, deleteTarget = null) }
	}

	fun dismissActionSheet() {
		_uiState.update { it.copy(actionTarget = null) }
	}

	fun onDeleteRequested(category: DomainCategory) {
		_uiState.update { it.copy(deleteTarget = category, actionTarget = null) }
	}

	fun dismissDeleteDialog() {
		_uiState.update { it.copy(deleteTarget = null) }
	}

	fun confirmDeleteCategory(category: DomainCategory) {
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }
			runCatching {
				val authorization = authorizationHeader()
					?: throw IllegalStateException("Sesi login tidak ditemukan")

				val response = categoryApi.deleteCategory(authorization, category.id)
				if (!response.isSuccessful) {
					val j = Json { ignoreUnknownKeys = true }
					throw IllegalStateException(response.errorMessage(j))
				}

				response.body() ?: throw IllegalStateException("Respons server kosong")
			}.onSuccess {
				_uiState.update { it.copy(isLoading = false, deleteTarget = null) }
				loadCategories()
			}.onFailure { throwable ->
				_uiState.update {
					it.copy(
						isLoading = false,
						errorMessage = throwable.message ?: "Gagal menghapus kategori",
					)
				}
			}
		}
	}

	private fun authorizationHeader(): String? {
		return authSessionProvider()?.let { "Bearer $it" }
	}

	init {
		loadCategories()
	}
}
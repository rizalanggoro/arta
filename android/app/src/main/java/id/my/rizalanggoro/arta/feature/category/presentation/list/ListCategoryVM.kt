package id.my.rizalanggoro.arta.feature.category.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListCategoryVM @Inject constructor(
    private val categoryApi: CategoryApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListCategoryUiState())
    val uiState: StateFlow<ListCategoryUiState> = _uiState.asStateFlow()

    fun loadCategories(type: String = _uiState.value.selectedType) {
        viewModelScope.launch {
            val selectedType = type.ifBlank { "expense" }
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    selectedType = selectedType
                )
            }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = categoryApi.listCategories(authorization)
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
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
                    throw IllegalStateException(response.errorMessage())
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
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    init {
        loadCategories()
    }
}
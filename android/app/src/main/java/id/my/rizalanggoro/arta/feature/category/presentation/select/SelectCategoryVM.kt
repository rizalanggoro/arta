package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
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
class SelectCategoryVM @Inject constructor(
    private val categoryApi: CategoryApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectCategoryUiState())
    val uiState: StateFlow<SelectCategoryUiState> = _uiState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                        isLoading = false
                    )
                }
            }.onFailure { throwable ->
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

    fun selectCategory(category: DomainCategory) {
        viewModelScope.launch {
            AppEventBus.emit(AppEvent.CategorySelected(category = category))
        }
    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    init {
        loadCategories()
    }
}
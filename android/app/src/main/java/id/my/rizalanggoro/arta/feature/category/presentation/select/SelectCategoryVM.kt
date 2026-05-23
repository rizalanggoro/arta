package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import kotlinx.serialization.json.Json
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import id.my.rizalanggoro.arta.openapi.models.DtoCategory
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectCategoryVM(
    private val categoryApi: CategoryApi,
    private val authSessionProvider: () -> String?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                SelectCategoryVM(
                    categoryApi = app.categoryApi,
                    authSessionProvider = { app.authPrefs.currentSession.value?.token },
                )
            }
        }
    }

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
                    val j = Json { ignoreUnknownKeys = true }
                    throw IllegalStateException(response.errorMessage(j))
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                _uiState.update { it.copy(categories = response.categories, isLoading = false) }
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
        return authSessionProvider()?.let { "Bearer $it" }
    }

    init {
        loadCategories()
    }
}

sealed interface SelectCategoryEffect {
    data object NavigateBack : SelectCategoryEffect
}
package id.my.rizalanggoro.arta.feature.category.presentation.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SelectCategoryVM.Factory::class)
class SelectCategoryVM @AssistedInject constructor(
    @Assisted private val navKey: CategoryRoute.Select,
    private val categoryApi: CategoryApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: CategoryRoute.Select): SelectCategoryVM
    }

    private val _uiState = MutableStateFlow(SelectCategoryUiState())
    val uiState: StateFlow<SelectCategoryUiState> = _uiState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val response = categoryApi.listCategories(
                    authorization = authPrefs.authorization()
                )

                if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                val selectedCategoryType = response
                    .categories
                    .firstOrNull { it.data.id == navKey.categoryId }
                    ?.data
                    ?.type ?: "income"

                _uiState.update {
                    it.copy(
                        categories = response.categories,
                        selectedType = selectedCategoryType
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        errorMessage = throwable.message ?: "Gagal memuat kategori"
                    )
                }
            }.also {
                _uiState.update {
                    it.copy(isLoading = false)
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

    init {
        loadCategories()
    }
}
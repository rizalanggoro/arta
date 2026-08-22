package id.my.rizalanggoro.arta.feature.category.presentation.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListCategoryVM @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val categoryApi: CategoryApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ListCategoryUiState())
    val uiState = _uiState.asStateFlow()

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
                val response = categoryApi.listCategories(
                    authorization = authPrefs.authorization()
                )
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException(
                    context.getString(R.string.server_empty_error)
                )
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        incomeCategories = response.categories.filter { it.data.type == "income" },
                        expenseCategories = response.categories.filter { it.data.type == "expense" },
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message
                            ?: context.getString(R.string.client_error),
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

    init {
        loadCategories()

        viewModelScope.launch {
            AppEventBus.event
                .filterIsInstance<AppEvent.CategoryChanged>()
                .collect { loadCategories() }
        }
    }
}

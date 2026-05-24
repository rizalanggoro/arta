package id.my.rizalanggoro.arta.feature.category.presentation.upsert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.Routes
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import id.my.rizalanggoro.arta.openapi.models.CategoryCreateCategoryReq
import id.my.rizalanggoro.arta.openapi.models.CategoryUpdateCategoryReq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = UpsertCategoryVM.Factory::class)
class UpsertCategoryVM @AssistedInject constructor(
    private val categoryApi: CategoryApi,
    private val authPrefs: AuthPrefs,
    @Assisted private val navKey: Routes.CategoryUpsertRoute
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: Routes.CategoryUpsertRoute): UpsertCategoryVM
    }

    private val _uiState = MutableStateFlow(UpsertCategoryUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCategory() {
        if (navKey.categoryId == 0) {
            _uiState.update { it.copy(isLoading = false, isUpdate = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = categoryApi.getCategory(authorization, navKey.categoryId)
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                val category = response.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isUpdate = true,
                        name = category.name,
                        type = category.type,
                        nameError = null,
                        errorMessage = null,
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

    fun onChangeName(value: String) {
        _uiState.update { it.copy(name = value, nameError = null, errorMessage = null) }
    }

    fun onChangeType(value: String) {
        _uiState.update { it.copy(type = value, errorMessage = null) }
    }

    fun submit() {
        val current = _uiState.value
        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Nama kategori wajib diisi") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when {
                current.isUpdate -> {
                    runCatching {
                        val authorization = authorizationHeader()
                            ?: throw IllegalStateException("Sesi login tidak ditemukan")

                        val response = categoryApi.updateCategory(
                            authorization = authorization,
                            id = navKey.categoryId,
                            body = CategoryUpdateCategoryReq(
                                name = current.name,
                                type = current.type,
                            ),
                        )

                        if (!response.isSuccessful) {
                            throw IllegalStateException(response.errorMessage())
                        }

                        response.body() ?: throw IllegalStateException("Respons server kosong")
                    }
                }

                else -> {
                    runCatching {
                        val authorization = authorizationHeader()
                            ?: throw IllegalStateException("Sesi login tidak ditemukan")

                        val response = categoryApi.createCategory(
                            authorization = authorization,
                            body = CategoryCreateCategoryReq(
                                name = current.name,
                                type = current.type,
                            ),
                        )

                        if (!response.isSuccessful) {
                            throw IllegalStateException(response.errorMessage())
                        }

                        response.body() ?: throw IllegalStateException("Respons server kosong")
                    }
                }
            }.onSuccess {
                AppEventBus.emit(AppEvent.CategoryChanged)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal menyimpan kategori",
                    )
                }
            }
        }
    }

    private fun authorizationHeader(): String? {
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }

    init {
        loadCategory()
    }
}
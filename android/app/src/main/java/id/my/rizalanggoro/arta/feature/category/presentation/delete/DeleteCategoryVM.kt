package id.my.rizalanggoro.arta.feature.category.presentation.delete

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.my.rizalanggoro.arta.R
import id.my.rizalanggoro.arta.core.application.route.CategoryRoute
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.event.AppEvent
import id.my.rizalanggoro.arta.core.event.AppEventBus
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DeleteCategoryVM.Factory::class)
class DeleteCategoryVM @AssistedInject constructor(
    @Assisted private val navKey: CategoryRoute.Delete,
    @param:ApplicationContext private val context: Context,
    private val authPrefs: AuthPrefs,
    private val categoryApi: CategoryApi,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: CategoryRoute.Delete): DeleteCategoryVM
    }

    private var _uiState = MutableStateFlow(DeleteCategoryUiState())
    val uiState = _uiState.asStateFlow()

    fun delete() = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }

        runCatching {
            val response = categoryApi.deleteCategory(
                authorization = authPrefs.authorization(),
                id = navKey.categoryId
            )

            if (response.isSuccessful.not()) throw IllegalStateException(response.errorMessage())

            response.body() ?: throw IllegalStateException(
                context.getString(R.string.server_empty_error)
            )
        }.onSuccess {
            AppEventBus.emit(AppEvent.CategoryChanged)
        }.onFailure {
        }.also {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}

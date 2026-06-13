package id.my.rizalanggoro.arta.feature.category.presentation.detail

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
import id.my.rizalanggoro.arta.core.extension.authorization
import id.my.rizalanggoro.arta.core.extension.toApiFormat
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailCategoryVM.Factory::class)
class DetailCategoryVM @AssistedInject constructor(
    @Assisted private val navKey: CategoryRoute.Detail,
    @param:ApplicationContext private val context: Context,
    private val authPrefs: AuthPrefs,
    private val categoryApi: CategoryApi,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(navKey: CategoryRoute.Detail): DetailCategoryVM
    }

    private var _uiState = MutableStateFlow(DetailCategoryUiState())
    val uiState = _uiState.asStateFlow()

    fun getCategory(isRefresh: Boolean = false) = viewModelScope.launch {
        runCatching {
            _uiState.update {
                it.copy(
                    isLoading = when (isRefresh) {
                        true -> it.isLoading
                        else -> true
                    },
                    isRefreshing = when (isRefresh) {
                        true -> true
                        else -> it.isRefreshing
                    }
                )
            }

            val response = categoryApi.getCategory(
                authorization = authPrefs.authorization(),
                categoryId = navKey.categoryId,
                includeTotalAmount = true,
                includeTransactions = true,
                startDate = navKey.transactionStartDateMillis.toApiFormat(),
                endDate = navKey.transactionEndDateMillis.toApiFormat(),
            )

            if (!response.isSuccessful) throw IllegalStateException(
                context.getString(
                    R.string.client_error
                )
            )

            response.body() ?: throw IllegalStateException(
                context.getString(
                    R.string.server_empty_error
                )
            )
        }.onSuccess { body ->
            _uiState.update { it.copy(category = body.item) }
        }.also {
            _uiState.update {
                it.copy(
                    isLoading = when (isRefresh) {
                        true -> it.isLoading
                        else -> false
                    },
                    isRefreshing = when (isRefresh) {
                        true -> false
                        else -> it.isRefreshing
                    }
                )
            }
        }
    }


    init {
        getCategory()
    }
}
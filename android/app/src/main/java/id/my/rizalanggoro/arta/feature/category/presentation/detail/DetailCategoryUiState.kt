package id.my.rizalanggoro.arta.feature.category.presentation.detail

import id.my.rizalanggoro.arta.openapi.models.DtoCategory

data class DetailCategoryUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val category: DtoCategory? = null,
)

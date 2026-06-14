package id.my.rizalanggoro.arta.feature.category.presentation.detail

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import id.my.rizalanggoro.arta.openapi.models.DtoCategory

data class DetailCategoryUiState(
    val selectedWallet: DomainWallet? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val category: DtoCategory? = null,
)

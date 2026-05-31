package id.my.rizalanggoro.arta.feature.category.presentation.list

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory

data class ListCategoryUiState(
    val incomeCategories: List<DtoCategory> = emptyList(),
    val expenseCategories: List<DtoCategory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedType: String = "income",
    val actionTarget: DomainCategory? = null,

    // delete
    val isDeleting: Boolean = false,
    val deleteTarget: DomainCategory? = null,
)
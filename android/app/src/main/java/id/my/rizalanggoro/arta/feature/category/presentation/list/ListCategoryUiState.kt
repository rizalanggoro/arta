package id.my.rizalanggoro.arta.feature.category.presentation.list

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DtoCategory

data class ListCategoryUiState(
	val categories: List<DtoCategory> = emptyList(),
	val isLoading: Boolean = false,
	val errorMessage: String? = null,
	val selectedType: String = "expense",
	val actionTarget: DomainCategory? = null,
	val deleteTarget: DomainCategory? = null,
)
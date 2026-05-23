package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import id.my.rizalanggoro.arta.openapi.models.DomainGold

data class GoldDetailUiState(
    val gold: DomainGold? = null,
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
)

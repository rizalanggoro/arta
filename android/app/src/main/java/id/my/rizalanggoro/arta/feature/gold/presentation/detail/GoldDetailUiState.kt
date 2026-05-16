package id.my.rizalanggoro.arta.feature.gold.presentation.detail

import id.my.rizalanggoro.arta.domain.Gold

data class GoldDetailUiState(
    val gold: Gold? = null,
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
)

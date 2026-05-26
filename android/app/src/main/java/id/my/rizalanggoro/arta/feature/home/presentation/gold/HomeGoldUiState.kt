package id.my.rizalanggoro.arta.feature.home.presentation.gold

import id.my.rizalanggoro.arta.openapi.models.DtoGold

data class HomeGoldUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val golds: List<DtoGold> = emptyList(),
)

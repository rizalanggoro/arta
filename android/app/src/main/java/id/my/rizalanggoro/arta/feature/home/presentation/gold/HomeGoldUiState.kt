package id.my.rizalanggoro.arta.feature.home.presentation.gold

import id.my.rizalanggoro.arta.domain.Gold

data class HomeGoldUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val golds: List<Gold> = emptyList(),
)

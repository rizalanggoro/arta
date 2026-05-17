package id.my.rizalanggoro.arta.feature.home.presentation.gold

import id.my.rizalanggoro.arta.domain.Gold

data class GoldListUiState(
    val title: String = "Emas",
    val description: String = "Daftar data emas dan riwayat perubahan gram akan tampil di sini.",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val golds: List<Gold> = emptyList(),
)

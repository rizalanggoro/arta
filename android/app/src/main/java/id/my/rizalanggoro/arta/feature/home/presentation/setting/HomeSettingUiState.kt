package id.my.rizalanggoro.arta.feature.home.presentation.setting

import id.my.rizalanggoro.arta.feature.auth.domain.AuthSession

data class HomeSettingUiState(
    val session: AuthSession? = null,
    val isDarkTheme: Boolean = false,
    val hasUpdate: Boolean = false,
)
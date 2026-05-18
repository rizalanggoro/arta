package id.my.rizalanggoro.arta.feature.auth.presentation.login

data class LoginUiState(
    val email: String = "[REDACTED]",
    val password: String = "[REDACTED]",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
)
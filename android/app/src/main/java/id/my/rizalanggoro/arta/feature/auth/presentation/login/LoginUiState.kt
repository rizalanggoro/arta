package id.my.rizalanggoro.arta.feature.auth.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
) {
    sealed class Event {
        data class ShowMessage(val message: String) : Event()
        data object LoginSucceeded : Event()
    }
}
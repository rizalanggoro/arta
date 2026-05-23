package id.my.rizalanggoro.arta.feature.auth.presentation.register

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
) {
    sealed class Event {
        data class ShowMessage(val message: String) : Event()
        data object RegisterSucceeded : Event()
    }
}
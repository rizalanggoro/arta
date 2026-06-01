package id.my.rizalanggoro.arta.feature.auth.presentation.logout

data class LogoutUiState(
    val isLoading: Boolean = false,
) {
    sealed class Event {
        data object LogoutSucceeded : Event()
    }
}
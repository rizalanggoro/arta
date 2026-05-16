package id.my.rizalanggoro.arta.feature.auth.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ForgotPasswordVM : ViewModel() {
	companion object {
		val Factory = viewModelFactory {
			initializer {
				ForgotPasswordVM()
			}
		}
	}

	private val _uiState = MutableStateFlow(ForgotPasswordUiState())
	val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()
}

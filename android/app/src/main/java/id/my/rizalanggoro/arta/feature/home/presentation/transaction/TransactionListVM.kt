package id.my.rizalanggoro.arta.feature.home.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransactionListVM : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer { TransactionListVM() }
        }
    }

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()
}

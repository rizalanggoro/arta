package id.my.rizalanggoro.arta.feature.transaction.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import id.my.rizalanggoro.arta.core.application.MyApplication
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionDetailVM(
    private val transactionApi: TransactionApi,
    private val authSessionProvider: () -> String?,
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as MyApplication)
                TransactionDetailVM(
                    transactionApi = app.transactionApi,
                    authSessionProvider = { app.authPrefs.currentSession.value?.token },
                )
            }
        }
    }

    private val _uiState = MutableStateFlow<DomainTransaction?>(null)
    val uiState: StateFlow<DomainTransaction?> = _uiState.asStateFlow()

    fun load(transactionId: Int) {
        viewModelScope.launch {
            runCatching {
                val authorization = authorizationHeader()
                    ?: throw IllegalStateException("Sesi login tidak ditemukan")

                val response = transactionApi.getTransaction(authorization, transactionId)
                if (!response.isSuccessful) {
                    throw IllegalStateException(response.errorMessage())
                }

                response.body() ?: throw IllegalStateException("Respons server kosong")
            }.onSuccess { response ->
                _uiState.update {
                    response.data
                }
            }
        }
    }

    private fun authorizationHeader(): String? {
        return authSessionProvider()?.let { "Bearer $it" }
    }
}

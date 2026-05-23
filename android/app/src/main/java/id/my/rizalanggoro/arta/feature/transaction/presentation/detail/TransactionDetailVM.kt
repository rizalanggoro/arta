package id.my.rizalanggoro.arta.feature.transaction.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.extension.errorMessage
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailVM @Inject constructor(
    private val transactionApi: TransactionApi,
    private val authPrefs: AuthPrefs,
) : ViewModel() {
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
        return authPrefs.currentSession.value?.token?.let { "Bearer $it" }
    }
}

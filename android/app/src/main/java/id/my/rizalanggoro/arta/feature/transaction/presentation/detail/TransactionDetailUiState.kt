package id.my.rizalanggoro.arta.feature.transaction.presentation.detail

import id.my.rizalanggoro.arta.openapi.models.DomainCategory
import id.my.rizalanggoro.arta.openapi.models.DomainTransaction

data class TransactionDetailUiState(
    val transaction: DomainTransaction? = null,
    val category: DomainCategory? = null,
)

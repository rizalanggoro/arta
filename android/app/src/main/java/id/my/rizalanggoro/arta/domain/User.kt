package id.my.rizalanggoro.arta.domain

import id.my.rizalanggoro.arta.openapi.models.DomainWallet
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val name: String,
    val password: String = "",
    val wallets: List<DomainWallet> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = "",
)

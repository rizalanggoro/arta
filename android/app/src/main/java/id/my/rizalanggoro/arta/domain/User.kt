package id.my.rizalanggoro.arta.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val email: String,
    val name: String,
    val password: String = "",
    val currency: String,
    val isActive: Boolean = false,
    val wallets: List<Wallet> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = "",
)

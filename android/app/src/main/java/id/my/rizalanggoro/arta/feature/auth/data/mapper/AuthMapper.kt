package id.my.rizalanggoro.arta.feature.auth.data.mapper

import id.my.rizalanggoro.arta.domain.AuthSession
import id.my.rizalanggoro.arta.feature.auth.data.dto.AuthResponseDto

fun AuthResponseDto.toDomain(): AuthSession {
    return AuthSession(
        userId = userId,
        email = email,
        name = name,
        currency = currency,
        token = token,
        tokenType = tokenType,
        expiresAt = expiresAt,
    )
}
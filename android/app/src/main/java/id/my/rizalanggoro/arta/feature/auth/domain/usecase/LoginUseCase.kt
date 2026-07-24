package id.my.rizalanggoro.arta.feature.auth.domain.usecase

import id.my.rizalanggoro.arta.feature.auth.domain.AuthSession
import id.my.rizalanggoro.arta.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthSession {
        return authRepository.login(email, password)
    }
}

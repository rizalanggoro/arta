package id.my.rizalanggoro.arta.feature.auth.domain.usecase

import id.my.rizalanggoro.arta.feature.auth.domain.AuthSession
import id.my.rizalanggoro.arta.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(name: String, email: String, password: String): AuthSession {
        return authRepository.register(name, email, password)
    }
}

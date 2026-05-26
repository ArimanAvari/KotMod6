package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.model.LoginSession
import com.example.kotmod6.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): LoginSession {
        return repository.login(username = username, password = password)
    }
}

package com.example.nobelapi.domain.usecase

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.nobelapi.domain.model.LoginRequest
import com.example.nobelapi.domain.model.LoginResponse
import com.example.nobelapi.domain.repository.NobelRepository
import com.example.nobelapi.security.JwtConfig

class LoginUseCase(
    private val repository: NobelRepository,
    private val jwtConfig: JwtConfig
) {
    fun execute(request: LoginRequest): LoginResponse? {
        val user = repository.findUser(request.username) ?: return null
        val verified = BCrypt.verifyer()
            .verify(request.password.toCharArray(), user.passwordHash)
            .verified

        return if (verified) {
            LoginResponse(token = jwtConfig.createToken(user.id, user.username))
        } else {
            null
        }
    }
}

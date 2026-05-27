package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.LoginRequest
import com.example.nobelapi.domain.model.LoginResponse
import com.example.nobelapi.security.JwtConfig

class LoginUseCase(
    private val jwtConfig: JwtConfig
) {
    fun execute(request: LoginRequest): LoginResponse? {
        val loginOk = (request.username == "admin" && request.password == "password123") ||
            (request.username == "student" && request.password == "studentpass")
        if (!loginOk) return null

        return LoginResponse(token = jwtConfig.createToken(request.username))
    }
}

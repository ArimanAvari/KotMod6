package com.example.nobelapi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresInMinutes: Int = 30
)

@Serializable
data class ApiError(
    val message: String
)

@Serializable
data class Laureate(
    val id: Int,
    val fullName: String,
    val birthCountry: String,
    val motivation: String
)

@Serializable
data class NobelPrize(
    val id: Int,
    val year: Int,
    val category: String,
    val description: String,
    val rawJson: String,
    val laureates: List<Laureate> = emptyList()
)

@Serializable
data class FavoriteResult(
    val message: String,
    val prize: NobelPrize? = null
)

data class AuthUser(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val role: String
)

class UnauthorizedProblem(message: String = "Нужна авторизация") : RuntimeException(message)
class NotFoundProblem(message: String = "Не найдено") : RuntimeException(message)

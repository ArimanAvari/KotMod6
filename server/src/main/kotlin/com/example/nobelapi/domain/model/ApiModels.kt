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
data class ErrorResponse(
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
    val year: Int,
    val category: String,
    val categoryFullName: String,
    val dateAwarded: String,
    val description: String,
    val laureates: List<Laureate>
)

class PrizeNotFoundException(year: Int, category: String) :
    RuntimeException("Премия $year/$category не найдена")

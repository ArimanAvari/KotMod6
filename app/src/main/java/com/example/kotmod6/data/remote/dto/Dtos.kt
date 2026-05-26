package com.example.kotmod6.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val token: String
)

@Serializable
data class LaureateDto(
    val id: Int,
    val fullName: String,
    val birthCountry: String,
    val motivation: String
)

@Serializable
data class NobelPrizeDto(
    val id: Int,
    val year: Int,
    val category: String,
    val description: String,
    val rawJson: String,
    val laureates: List<LaureateDto> = emptyList()
)

@Serializable
data class FavoriteResultDto(
    val message: String,
    val prize: NobelPrizeDto? = null
)

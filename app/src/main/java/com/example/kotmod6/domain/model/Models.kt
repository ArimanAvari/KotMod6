package com.example.kotmod6.domain.model

data class Laureate(
    val id: Int,
    val fullName: String,
    val birthCountry: String,
    val motivation: String
)

data class NobelPrize(
    val id: Int,
    val year: Int,
    val category: String,
    val description: String,
    val rawJson: String,
    val laureates: List<Laureate>
) {
    val title: String = "$year · ${category.replaceFirstChar { it.uppercase() }}"
}

data class LoginSession(
    val token: String
)

data class FavoriteResult(
    val message: String,
    val prize: NobelPrize?
)

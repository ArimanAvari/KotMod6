package com.example.kotmod6.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LaureateDetailDto(
    val id: String = "",
    val knownName: LocalizedTextDto? = null,
    val fullName: LocalizedTextDto? = null,
    val fileName: String? = null,
    val birth: BirthDto? = null,
    val wikipedia: WikipediaDto? = null,
    val links: List<LinkDto> = emptyList(),
    val nobelPrizes: List<LaureatePrizeDetailDto> = emptyList()
)

@Serializable
data class BirthDto(
    val place: LocationDto? = null
)

@Serializable
data class WikipediaDto(
    val english: String? = null
)

@Serializable
data class LaureatePrizeDetailDto(
    val awardYear: String = "",
    val category: LocalizedTextDto? = null,
    val motivation: LocalizedTextDto? = null,
    val links: List<LinkDto> = emptyList()
)

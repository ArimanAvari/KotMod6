package com.example.kotmod6.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizeResponseDto(
    val nobelPrizes: List<NobelPrizeDto> = emptyList()
)

@Serializable
data class NobelPrizeDto(
    val awardYear: String = "",
    val category: LocalizedTextDto? = null,
    val links: List<LinkDto> = emptyList(),
    val laureates: List<PrizeLaureateDto> = emptyList()
)

@Serializable
data class PrizeLaureateDto(
    val id: String = "",
    val knownName: LocalizedTextDto? = null,
    val fullName: LocalizedTextDto? = null,
    val motivation: LocalizedTextDto? = null,
    val links: List<LinkDto> = emptyList()
)

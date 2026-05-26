package com.example.kotmod6.data.mapper

import com.example.kotmod6.data.remote.dto.FavoriteResultDto
import com.example.kotmod6.data.remote.dto.LaureateDto
import com.example.kotmod6.data.remote.dto.NobelPrizeDto
import com.example.kotmod6.domain.model.FavoriteResult
import com.example.kotmod6.domain.model.Laureate
import com.example.kotmod6.domain.model.NobelPrize

fun NobelPrizeDto.toDomain(): NobelPrize {
    return NobelPrize(
        id = id,
        year = year,
        category = category,
        description = description,
        rawJson = rawJson,
        laureates = laureates.map { it.toDomain() }
    )
}

fun LaureateDto.toDomain(): Laureate {
    return Laureate(
        id = id,
        fullName = fullName,
        birthCountry = birthCountry,
        motivation = motivation
    )
}

fun FavoriteResultDto.toDomain(): FavoriteResult {
    return FavoriteResult(
        message = message,
        prize = prize?.toDomain()
    )
}

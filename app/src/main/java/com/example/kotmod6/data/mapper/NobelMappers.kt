package com.example.kotmod6.data.mapper

import com.example.kotmod6.data.remote.dto.LaureateDetailDto
import com.example.kotmod6.data.remote.dto.LaureatePrizeDetailDto
import com.example.kotmod6.data.remote.dto.NobelPrizeDto
import com.example.kotmod6.domain.model.LaureateDetail
import com.example.kotmod6.domain.model.LaureatePrize

fun NobelPrizeDto.toLaureatePrizes(categoryCode: String): List<LaureatePrize> {
    val categoryName = category?.en.orEmpty()
    return laureates.map { laureate ->
        LaureatePrize(
            laureateId = laureate.id,
            fullName = laureate.fullName?.en ?: laureate.knownName?.en ?: "Unknown laureate",
            awardYear = awardYear,
            category = categoryName.ifBlank { "Unknown category" },
            categoryCode = categoryCode,
            motivation = laureate.motivation?.en.orEmpty(),
            sourceUrl = laureate.links.firstOrNull { it.rel == "laureate" }?.href
        )
    }
}

fun LaureateDetailDto.toDomain(year: String, categoryCode: String): LaureateDetail {
    val prize = findPrize(year = year, categoryCode = categoryCode)
    val fallbackPrize = nobelPrizes.firstOrNull()
    val birthPlace = birth?.place?.locationString?.en
        ?: listOfNotNull(birth?.place?.city?.en, birth?.place?.country?.en).joinToString(", ")
            .ifBlank { "Не указано" }

    return LaureateDetail(
        id = id,
        fullName = fullName?.en ?: knownName?.en ?: "Unknown laureate",
        awardYear = prize?.awardYear ?: fallbackPrize?.awardYear ?: year,
        category = prize?.category?.en ?: fallbackPrize?.category?.en ?: "Unknown category",
        motivation = prize?.motivation?.en ?: fallbackPrize?.motivation?.en.orEmpty(),
        birthPlace = birthPlace,
        portraitUrl = null,
        wikipediaUrl = wikipedia?.english,
        nobelPageUrl = links.firstOrNull { it.rel == "external" }?.href
    )
}

private fun LaureateDetailDto.findPrize(year: String, categoryCode: String): LaureatePrizeDetailDto? {
    return nobelPrizes.firstOrNull { prize ->
        prize.awardYear == year && prize.links.any { link -> link.href?.contains("/$categoryCode/$year") == true }
    }
}

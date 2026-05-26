package com.example.kotmod6.data.repository

import com.example.kotmod6.data.mapper.toDomain
import com.example.kotmod6.data.mapper.toLaureatePrizes
import com.example.kotmod6.data.remote.NobelApi
import com.example.kotmod6.domain.model.LaureateDetail
import com.example.kotmod6.domain.model.LaureatePrize
import com.example.kotmod6.domain.repository.NobelRepository

class ApiNobelRepository(
    private val api: NobelApi
) : NobelRepository {
    override suspend fun loadLaureates(year: String?, categoryCode: String?): List<LaureatePrize> {
        val response = api.getPrizes(year = year, categoryCode = categoryCode)
        return response.nobelPrizes.flatMap { prize ->
            val code = categoryCode?.takeIf { it.isNotBlank() } ?: prize.links.firstOrNull()
                ?.href
                ?.split("/")
                ?.takeLast(2)
                ?.firstOrNull()
                .orEmpty()

            prize.toLaureatePrizes(code)
        }
    }

    override suspend fun loadLaureateDetail(
        id: String,
        year: String,
        categoryCode: String
    ): LaureateDetail {
        val dto = api.getLaureate(id).firstOrNull()
            ?: error("Лауреат не найден")
        return dto.toDomain(year = year, categoryCode = categoryCode)
    }
}

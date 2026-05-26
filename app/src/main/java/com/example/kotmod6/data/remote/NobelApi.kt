package com.example.kotmod6.data.remote

import com.example.kotmod6.data.remote.dto.LaureateDetailDto
import com.example.kotmod6.data.remote.dto.NobelPrizeResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NobelApi(
    private val client: HttpClient
) {
    suspend fun getPrizes(year: String?, categoryCode: String?): NobelPrizeResponseDto {
        return client.get("https://api.nobelprize.org/2.1/nobelPrizes") {
            parameter("limit", 25)
            parameter("offset", 0)
            if (!year.isNullOrBlank()) {
                parameter("nobelPrizeYear", year)
            }
            if (!categoryCode.isNullOrBlank()) {
                parameter("nobelPrizeCategory", categoryCode)
            }
        }.body()
    }

    suspend fun getLaureate(id: String): List<LaureateDetailDto> {
        return client.get("https://api.nobelprize.org/2.1/laureate/$id").body()
    }
}

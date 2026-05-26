package com.example.nobelapi.presentation

import com.example.nobelapi.domain.model.ApiError
import com.example.nobelapi.domain.usecase.GetLaureatesUseCase
import com.example.nobelapi.domain.usecase.GetPrizeDetailUseCase
import com.example.nobelapi.domain.usecase.GetPrizesUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

class PrizeController(
    private val getPrizesUseCase: GetPrizesUseCase,
    private val getPrizeDetailUseCase: GetPrizeDetailUseCase,
    private val getLaureatesUseCase: GetLaureatesUseCase
) {
    suspend fun all(call: ApplicationCall) {
        call.respond(getPrizesUseCase.execute())
    }

    suspend fun detail(call: ApplicationCall) {
        val year = call.yearParam() ?: return
        val category = call.categoryParam() ?: return
        call.respond(getPrizeDetailUseCase.execute(year, category))
    }

    suspend fun laureates(call: ApplicationCall) {
        val year = call.yearParam() ?: return
        val category = call.categoryParam() ?: return
        call.respond(getLaureatesUseCase.execute(year, category))
    }

    private suspend fun ApplicationCall.yearParam(): Int? {
        val year = parameters["year"]?.toIntOrNull()
        if (year == null) {
            respond(HttpStatusCode.BadRequest, ApiError("Год должен быть числом"))
        }
        return year
    }

    private suspend fun ApplicationCall.categoryParam(): String? {
        val category = parameters["category"]?.trim()?.lowercase()
        if (category.isNullOrBlank()) {
            respond(HttpStatusCode.BadRequest, ApiError("Категория не указана"))
            return null
        }
        return category
    }
}

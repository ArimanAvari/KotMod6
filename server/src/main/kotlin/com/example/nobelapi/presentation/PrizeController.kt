package com.example.nobelapi.presentation

import com.example.nobelapi.domain.model.ErrorResponse
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
    suspend fun getPrizes(call: ApplicationCall) {
        call.respond(getPrizesUseCase.execute())
    }

    suspend fun getPrizeDetail(call: ApplicationCall) {
        val year = call.yearParameter() ?: return
        val category = call.categoryParameter() ?: return

        call.respond(getPrizeDetailUseCase.execute(year, category))
    }

    suspend fun getLaureates(call: ApplicationCall) {
        val year = call.yearParameter() ?: return
        val category = call.categoryParameter() ?: return

        call.respond(getLaureatesUseCase.execute(year, category))
    }

    private suspend fun ApplicationCall.yearParameter(): Int? {
        val year = parameters["year"]?.toIntOrNull()
        if (year == null) {
            respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse("Год должен быть числом")
            )
        }
        return year
    }

    private suspend fun ApplicationCall.categoryParameter(): String? {
        val category = parameters["category"]?.trim()?.lowercase()
        if (category.isNullOrBlank()) {
            respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse("Категория не указана")
            )
            return null
        }
        return category
    }
}

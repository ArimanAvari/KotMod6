package com.example.nobelapi.presentation

import com.example.nobelapi.domain.model.FavoriteResult
import com.example.nobelapi.domain.model.UnauthorizedProblem
import com.example.nobelapi.domain.usecase.FavoriteUseCases
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond

class FavoriteController(
    private val favoriteUseCases: FavoriteUseCases
) {
    suspend fun list(call: ApplicationCall) {
        call.respond(favoriteUseCases.list(call.userId()))
    }

    suspend fun add(call: ApplicationCall) {
        val year = call.yearParam()
        val category = call.categoryParam()
        val prize = favoriteUseCases.add(call.userId(), year, category)
        call.respond(FavoriteResult("Премия добавлена в избранное", prize))
    }

    suspend fun remove(call: ApplicationCall) {
        val year = call.yearParam()
        val category = call.categoryParam()
        favoriteUseCases.remove(call.userId(), year, category)
        call.respond(FavoriteResult("Премия удалена из избранного"))
    }

    private fun ApplicationCall.userId(): Int {
        return principal<JWTPrincipal>()
            ?.payload
            ?.getClaim("userId")
            ?.asInt()
            ?: throw UnauthorizedProblem()
    }

    private fun ApplicationCall.yearParam(): Int {
        return parameters["year"]?.toIntOrNull()
            ?: throw BadRequestException("Год должен быть числом")
    }

    private fun ApplicationCall.categoryParam(): String {
        return parameters["category"]?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
            ?: throw BadRequestException("Категория не указана")
    }
}

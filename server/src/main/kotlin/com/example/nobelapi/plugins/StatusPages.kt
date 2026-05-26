package com.example.nobelapi.plugins

import com.example.nobelapi.domain.model.ApiError
import com.example.nobelapi.domain.model.NotFoundProblem
import com.example.nobelapi.domain.model.UnauthorizedProblem
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ApiError(cause.message ?: "Некорректный запрос"))
        }
        exception<UnauthorizedProblem> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, ApiError(cause.message ?: "Нужна авторизация"))
        }
        exception<NotFoundProblem> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ApiError(cause.message ?: "Не найдено"))
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, ApiError(cause.message ?: "Ошибка сервера"))
        }
    }
}

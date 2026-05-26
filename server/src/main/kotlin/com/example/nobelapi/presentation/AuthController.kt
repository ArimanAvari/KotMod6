package com.example.nobelapi.presentation

import com.example.nobelapi.domain.model.ErrorResponse
import com.example.nobelapi.domain.model.LoginRequest
import com.example.nobelapi.domain.usecase.LoginUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class AuthController(
    private val loginUseCase: LoginUseCase
) {
    suspend fun login(call: ApplicationCall) {
        val request = call.receive<LoginRequest>()
        val response = loginUseCase.execute(request)

        if (response == null) {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = ErrorResponse("Неверный логин или пароль")
            )
        } else {
            call.respond(response)
        }
    }
}

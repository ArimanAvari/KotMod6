package com.example.nobelapi.routing

import com.example.nobelapi.di.AppModule
import com.example.nobelapi.domain.model.ErrorResponse
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRoutes(appModule: AppModule) {
    routing {
        get("/") {
            call.respond(ErrorResponse("Nobel Prize API работает. Используйте POST /auth/login"))
        }

        authRoutes(appModule.authController)
        prizeRoutes(appModule.prizeController)
    }
}

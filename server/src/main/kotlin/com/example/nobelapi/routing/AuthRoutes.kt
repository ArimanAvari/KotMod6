package com.example.nobelapi.routing

import com.example.nobelapi.presentation.AuthController
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes(controller: AuthController) {
    post("/login") {
        controller.login(call)
    }

    post("/auth/login") {
        controller.login(call)
    }
}

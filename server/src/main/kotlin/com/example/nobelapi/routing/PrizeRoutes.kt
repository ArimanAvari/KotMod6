package com.example.nobelapi.routing

import com.example.nobelapi.presentation.PrizeController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.prizeRoutes(controller: PrizeController) {
    authenticate("auth-jwt") {
        get("/prizes") {
            controller.getPrizes(call)
        }

        get("/prizes/{year}/{category}") {
            controller.getPrizeDetail(call)
        }

        get("/prizes/{year}/{category}/laureates") {
            controller.getLaureates(call)
        }
    }
}

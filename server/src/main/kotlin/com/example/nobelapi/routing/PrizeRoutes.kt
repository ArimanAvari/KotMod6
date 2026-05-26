package com.example.nobelapi.routing

import com.example.nobelapi.presentation.FavoriteController
import com.example.nobelapi.presentation.PrizeController
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.prizeRoutes(
    prizeController: PrizeController,
    favoriteController: FavoriteController
) {
    authenticate("auth-jwt") {
        get("/prizes") {
            prizeController.all(call)
        }

        get("/prizes/{year}/{category}") {
            prizeController.detail(call)
        }

        get("/prizes/{year}/{category}/laureates") {
            prizeController.laureates(call)
        }

        get("/favorites") {
            favoriteController.list(call)
        }

        post("/favorites/{year}/{category}") {
            favoriteController.add(call)
        }

        delete("/favorites/{year}/{category}") {
            favoriteController.remove(call)
        }
    }
}

package com.example.nobelapi

import com.example.nobelapi.data.database.DatabaseFactory
import com.example.nobelapi.di.AppModule
import com.example.nobelapi.plugins.configureAuthentication
import com.example.nobelapi.plugins.configureCallLogging
import com.example.nobelapi.plugins.configureContentNegotiation
import com.example.nobelapi.plugins.configureCors
import com.example.nobelapi.plugins.configureStatusPages
import com.example.nobelapi.routing.configureRoutes
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(
        factory = Netty,
        port = System.getenv("PORT")?.toIntOrNull() ?: 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val appModule = AppModule()

    configureContentNegotiation()
    configureCallLogging()
    configureCors()
    configureStatusPages()
    configureAuthentication(appModule.jwtConfig)
    configureRoutes(appModule)
}

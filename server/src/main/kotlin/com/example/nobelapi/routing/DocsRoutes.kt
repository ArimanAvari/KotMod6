package com.example.nobelapi.routing

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.docsRoutes() {
    get("/docs") {
        call.respondText(docsHtml, ContentType.Text.Html)
    }
    get("/openapi.json") {
        call.respondText(openApiJson, ContentType.Application.Json)
    }
}

private val docsHtml = """
    <!doctype html>
    <html>
      <head>
        <title>Nobel Prize API Docs</title>
        <meta charset="utf-8"/>
      </head>
      <body>
        <redoc spec-url="/openapi.json"></redoc>
        <script src="https://cdn.redoc.ly/redoc/latest/bundles/redoc.standalone.js"></script>
      </body>
    </html>
""".trimIndent()

private val openApiJson = """
{
  "openapi": "3.0.3",
  "info": {
    "title": "Nobel Prize API",
    "version": "1.0.0"
  },
  "paths": {
    "/auth/login": {
      "post": {
        "summary": "Login and receive JWT",
        "requestBody": {
          "required": true,
          "content": {
            "application/json": {
              "example": {
                "username": "student",
                "password": "studentpass"
              }
            }
          }
        },
        "responses": {
          "200": { "description": "JWT token" },
          "401": { "description": "Invalid credentials" }
        }
      }
    },
    "/prizes": {
      "get": {
        "summary": "List prizes",
        "security": [{ "bearerAuth": [] }],
        "responses": {
          "200": { "description": "Prize list" },
          "401": { "description": "Unauthorized" }
        }
      }
    },
    "/prizes/{year}/{category}": {
      "get": {
        "summary": "Prize details",
        "security": [{ "bearerAuth": [] }],
        "parameters": [
          { "name": "year", "in": "path", "required": true, "schema": { "type": "integer" } },
          { "name": "category", "in": "path", "required": true, "schema": { "type": "string" } }
        ],
        "responses": {
          "200": { "description": "Prize detail" },
          "404": { "description": "Prize not found" }
        }
      }
    },
    "/prizes/{year}/{category}/laureates": {
      "get": {
        "summary": "Prize laureates",
        "security": [{ "bearerAuth": [] }],
        "parameters": [
          { "name": "year", "in": "path", "required": true, "schema": { "type": "integer" } },
          { "name": "category", "in": "path", "required": true, "schema": { "type": "string" } }
        ],
        "responses": {
          "200": { "description": "Laureate list" }
        }
      }
    },
    "/favorites": {
      "get": {
        "summary": "Current user's favorite prizes",
        "security": [{ "bearerAuth": [] }],
        "responses": {
          "200": { "description": "Favorite list" }
        }
      }
    },
    "/favorites/{year}/{category}": {
      "post": {
        "summary": "Add favorite prize",
        "security": [{ "bearerAuth": [] }],
        "parameters": [
          { "name": "year", "in": "path", "required": true, "schema": { "type": "integer" } },
          { "name": "category", "in": "path", "required": true, "schema": { "type": "string" } }
        ],
        "responses": {
          "200": { "description": "Favorite added" }
        }
      },
      "delete": {
        "summary": "Delete favorite prize",
        "security": [{ "bearerAuth": [] }],
        "parameters": [
          { "name": "year", "in": "path", "required": true, "schema": { "type": "integer" } },
          { "name": "category", "in": "path", "required": true, "schema": { "type": "string" } }
        ],
        "responses": {
          "200": { "description": "Favorite deleted" },
          "404": { "description": "Favorite not found" }
        }
      }
    }
  },
  "components": {
    "securitySchemes": {
      "bearerAuth": {
        "type": "http",
        "scheme": "bearer",
        "bearerFormat": "JWT"
      }
    }
  }
}
""".trimIndent()

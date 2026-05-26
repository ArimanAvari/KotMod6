package com.example.nobelapi.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date

class JwtConfig {
    private val secret = System.getenv("JWT_SECRET")
        ?: "task-five-neon-api-secret-key-32-plus"
    private val issuer = "kotmod6-nobel-api-db"
    private val audience = "kotmod6-users"
    val realm = "nobel-api"
    private val expiresInMs = 30 * 60 * 1000L
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun createToken(userId: Int, username: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("username", username)
            .withExpiresAt(Date(now + expiresInMs))
            .sign(algorithm)
    }

    fun validate(credential: JWTCredential): JWTPrincipal? {
        val userId = credential.payload.getClaim("userId").asInt()
        return if (userId != null) JWTPrincipal(credential.payload) else null
    }
}

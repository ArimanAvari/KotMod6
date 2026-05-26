package com.example.kotmod6.data.remote

import com.example.kotmod6.data.remote.dto.FavoriteResultDto
import com.example.kotmod6.data.remote.dto.LoginRequestDto
import com.example.kotmod6.data.remote.dto.LoginResponseDto
import com.example.kotmod6.data.remote.dto.NobelPrizeDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NobelServerApi(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun login(username: String, password: String): LoginResponseDto {
        return client.post(baseUrl + "auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username, password))
        }.body()
    }

    suspend fun prizes(token: String): List<NobelPrizeDto> {
        return client.get(baseUrl + "prizes") {
            bearerAuth(token)
        }.body()
    }

    suspend fun prize(token: String, year: Int, category: String): NobelPrizeDto {
        return client.get(baseUrl + "prizes/$year/$category") {
            bearerAuth(token)
        }.body()
    }

    suspend fun favorites(token: String): List<NobelPrizeDto> {
        return client.get(baseUrl + "favorites") {
            bearerAuth(token)
        }.body()
    }

    suspend fun addFavorite(token: String, year: Int, category: String): FavoriteResultDto {
        return client.post(baseUrl + "favorites/$year/$category") {
            bearerAuth(token)
        }.body()
    }

    suspend fun removeFavorite(token: String, year: Int, category: String): FavoriteResultDto {
        return client.delete(baseUrl + "favorites/$year/$category") {
            bearerAuth(token)
        }.body()
    }
}

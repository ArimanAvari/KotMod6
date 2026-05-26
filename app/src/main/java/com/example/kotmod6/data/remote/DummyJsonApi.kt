package com.example.kotmod6.data.remote

import com.example.kotmod6.data.remote.dto.LoginRequestDto
import com.example.kotmod6.data.remote.dto.LoginResponseDto
import com.example.kotmod6.data.remote.dto.UserDto
import com.example.kotmod6.data.remote.dto.UsersResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DummyJsonApi(
    private val client: HttpClient
) {
    suspend fun login(username: String, password: String): LoginResponseDto {
        return client.post("https://dummyjson.com/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username = username, password = password))
        }.body()
    }

    suspend fun getUsers(token: String): UsersResponseDto {
        return client.get("https://dummyjson.com/users") {
            bearerAuth(token)
        }.body()
    }

    suspend fun getUser(id: Int, token: String): UserDto {
        return client.get("https://dummyjson.com/users/$id") {
            bearerAuth(token)
        }.body()
    }
}

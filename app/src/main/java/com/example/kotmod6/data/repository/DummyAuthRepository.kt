package com.example.kotmod6.data.repository

import com.example.kotmod6.data.local.TokenStorage
import com.example.kotmod6.data.mapper.toDomain
import com.example.kotmod6.data.mapper.toSession
import com.example.kotmod6.data.remote.DummyJsonApi
import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.domain.model.LoginSession
import com.example.kotmod6.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DummyAuthRepository(
    private val api: DummyJsonApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    override val tokenFlow: Flow<String?> = tokenStorage.tokenFlow

    override suspend fun login(username: String, password: String): LoginSession {
        val session = api.login(username = username, password = password).toSession()
        if (session.token.isBlank()) {
            error("Сервер не вернул токен")
        }
        tokenStorage.saveToken(session.token)
        return session
    }

    override suspend fun loadUsers(): List<AppUser> {
        return api.getUsers(requireToken()).users.map { it.toDomain() }
    }

    override suspend fun loadUser(id: Int): AppUser {
        return api.getUser(id = id, token = requireToken()).toDomain()
    }

    override suspend fun logout() {
        tokenStorage.clearToken()
    }

    private suspend fun requireToken(): String {
        return tokenStorage.tokenFlow.first()?.takeIf { it.isNotBlank() }
            ?: error("Нужно войти в аккаунт")
    }
}

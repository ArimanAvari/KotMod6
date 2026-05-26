package com.example.kotmod6.data.repository

import com.example.kotmod6.data.local.TokenStorage
import com.example.kotmod6.data.mapper.toDomain
import com.example.kotmod6.data.remote.NobelServerApi
import com.example.kotmod6.domain.model.LoginSession
import com.example.kotmod6.domain.repository.NobelClientRepository
import kotlinx.coroutines.flow.first

class ServerNobelRepository(
    private val api: NobelServerApi,
    private val tokenStorage: TokenStorage
) : NobelClientRepository {
    override val tokenFlow = tokenStorage.tokenFlow

    override suspend fun login(username: String, password: String): LoginSession {
        val response = api.login(username, password)
        tokenStorage.save(response.token)
        return LoginSession(response.token)
    }

    override suspend fun loadPrizes() = api.prizes(requireToken()).map { it.toDomain() }

    override suspend fun loadPrize(year: Int, category: String) =
        api.prize(requireToken(), year, category).toDomain()

    override suspend fun loadFavorites() = api.favorites(requireToken()).map { it.toDomain() }

    override suspend fun addFavorite(year: Int, category: String) =
        api.addFavorite(requireToken(), year, category).toDomain()

    override suspend fun removeFavorite(year: Int, category: String) =
        api.removeFavorite(requireToken(), year, category).toDomain()

    override suspend fun logout() {
        tokenStorage.clear()
    }

    private suspend fun requireToken(): String {
        return tokenFlow.first()?.takeIf { it.isNotBlank() }
            ?: error("Нужно войти в аккаунт")
    }
}

package com.example.kotmod6.domain.repository

import com.example.kotmod6.domain.model.FavoriteResult
import com.example.kotmod6.domain.model.LoginSession
import com.example.kotmod6.domain.model.NobelPrize
import kotlinx.coroutines.flow.Flow

interface NobelClientRepository {
    val tokenFlow: Flow<String?>

    suspend fun login(username: String, password: String): LoginSession
    suspend fun loadPrizes(): List<NobelPrize>
    suspend fun loadPrize(year: Int, category: String): NobelPrize
    suspend fun loadFavorites(): List<NobelPrize>
    suspend fun addFavorite(year: Int, category: String): FavoriteResult
    suspend fun removeFavorite(year: Int, category: String): FavoriteResult
    suspend fun logout()
}

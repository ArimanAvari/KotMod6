package com.example.nobelapi.domain.repository

import com.example.nobelapi.domain.model.AuthUser
import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize

interface NobelRepository {
    fun findUser(username: String): AuthUser?
    fun getPrizes(): List<NobelPrize>
    fun getPrize(year: Int, category: String): NobelPrize?
    fun getLaureates(year: Int, category: String): List<Laureate>?
    fun getFavorites(userId: Int): List<NobelPrize>
    fun addFavorite(userId: Int, year: Int, category: String): NobelPrize?
    fun removeFavorite(userId: Int, year: Int, category: String): Boolean
}

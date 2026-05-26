package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.model.NotFoundProblem
import com.example.nobelapi.domain.repository.NobelRepository

class GetPrizesUseCase(
    private val repository: NobelRepository
) {
    fun execute(): List<NobelPrize> = repository.getPrizes()
}

class GetPrizeDetailUseCase(
    private val repository: NobelRepository
) {
    fun execute(year: Int, category: String): NobelPrize {
        return repository.getPrize(year, category)
            ?: throw NotFoundProblem("Премия $year/$category не найдена")
    }
}

class GetLaureatesUseCase(
    private val repository: NobelRepository
) {
    fun execute(year: Int, category: String): List<Laureate> {
        return repository.getLaureates(year, category)
            ?: throw NotFoundProblem("Лауреаты для $year/$category не найдены")
    }
}

class FavoriteUseCases(
    private val repository: NobelRepository
) {
    fun list(userId: Int): List<NobelPrize> = repository.getFavorites(userId)

    fun add(userId: Int, year: Int, category: String): NobelPrize {
        return repository.addFavorite(userId, year, category)
            ?: throw NotFoundProblem("Премия $year/$category не найдена")
    }

    fun remove(userId: Int, year: Int, category: String): Boolean {
        val removed = repository.removeFavorite(userId, year, category)
        if (!removed) {
            throw NotFoundProblem("Премия $year/$category не была в избранном")
        }
        return true
    }
}

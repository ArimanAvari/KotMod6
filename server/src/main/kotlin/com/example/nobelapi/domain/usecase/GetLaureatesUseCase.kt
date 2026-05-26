package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.PrizeNotFoundException
import com.example.nobelapi.domain.repository.PrizeRepository

class GetLaureatesUseCase(
    private val repository: PrizeRepository
) {
    fun execute(year: Int, category: String): List<Laureate> {
        return repository.getLaureates(year = year, category = category)
            ?: throw PrizeNotFoundException(year, category)
    }
}

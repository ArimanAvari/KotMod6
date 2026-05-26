package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.model.PrizeNotFoundException
import com.example.nobelapi.domain.repository.PrizeRepository

class GetPrizeDetailUseCase(
    private val repository: PrizeRepository
) {
    fun execute(year: Int, category: String): NobelPrize {
        return repository.find(year = year, category = category)
            ?: throw PrizeNotFoundException(year, category)
    }
}

package com.example.nobelapi.domain.usecase

import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.PrizeRepository

class GetPrizesUseCase(
    private val repository: PrizeRepository
) {
    fun execute(): List<NobelPrize> = repository.getAll()
}

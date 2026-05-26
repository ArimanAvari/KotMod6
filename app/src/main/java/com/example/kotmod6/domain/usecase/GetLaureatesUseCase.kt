package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.model.LaureatePrize
import com.example.kotmod6.domain.repository.NobelRepository

class GetLaureatesUseCase(
    private val repository: NobelRepository
) {
    suspend operator fun invoke(year: String?, categoryCode: String?): List<LaureatePrize> {
        return repository.loadLaureates(year = year, categoryCode = categoryCode)
    }
}

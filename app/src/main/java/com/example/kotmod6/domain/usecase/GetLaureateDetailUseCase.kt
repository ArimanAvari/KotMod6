package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.model.LaureateDetail
import com.example.kotmod6.domain.repository.NobelRepository

class GetLaureateDetailUseCase(
    private val repository: NobelRepository
) {
    suspend operator fun invoke(id: String, year: String, categoryCode: String): LaureateDetail {
        return repository.loadLaureateDetail(id = id, year = year, categoryCode = categoryCode)
    }
}

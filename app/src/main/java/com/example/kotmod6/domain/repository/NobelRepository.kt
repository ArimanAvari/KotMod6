package com.example.kotmod6.domain.repository

import com.example.kotmod6.domain.model.LaureateDetail
import com.example.kotmod6.domain.model.LaureatePrize

interface NobelRepository {
    suspend fun loadLaureates(year: String?, categoryCode: String?): List<LaureatePrize>
    suspend fun loadLaureateDetail(id: String, year: String, categoryCode: String): LaureateDetail
}

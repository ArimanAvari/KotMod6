package com.example.nobelapi.domain.repository

import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize

interface PrizeRepository {
    fun getAll(): List<NobelPrize>
    fun find(year: Int, category: String): NobelPrize?
    fun getLaureates(year: Int, category: String): List<Laureate>?
}

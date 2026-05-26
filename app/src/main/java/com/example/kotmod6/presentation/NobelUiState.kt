package com.example.kotmod6.presentation

import com.example.kotmod6.domain.model.LaureateDetail
import com.example.kotmod6.domain.model.LaureatePrize
import com.example.kotmod6.domain.model.PrizeCategory

data class FilterState(
    val year: String = "",
    val selectedCategory: PrizeCategory = PrizeCategory.All
)

sealed interface LaureateListState {
    data object Loading : LaureateListState
    data class Success(val laureates: List<LaureatePrize>) : LaureateListState
    data class Error(val message: String) : LaureateListState
}

sealed interface LaureateDetailState {
    data object Idle : LaureateDetailState
    data object Loading : LaureateDetailState
    data class Success(val detail: LaureateDetail) : LaureateDetailState
    data class Error(val message: String) : LaureateDetailState
}

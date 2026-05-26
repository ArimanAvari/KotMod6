package com.example.kotmod6.presentation

import com.example.kotmod6.domain.model.NobelPrize

enum class AppScreen {
    Login,
    List,
    Detail,
    Favorites
}

data class NobelClientState(
    val checkingToken: Boolean = true,
    val screen: AppScreen = AppScreen.Login,
    val username: String = "student",
    val password: String = "studentpass",
    val filterYear: String = "",
    val filterCategory: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val prizes: List<NobelPrize> = emptyList(),
    val favorites: List<NobelPrize> = emptyList(),
    val selectedPrize: NobelPrize? = null
) {
    val visiblePrizes: List<NobelPrize>
        get() = prizes.filter { prize ->
            val yearOk = filterYear.isBlank() || prize.year.toString().contains(filterYear)
            val categoryOk = filterCategory.isBlank() || prize.category.contains(filterCategory, ignoreCase = true)
            yearOk && categoryOk
        }
}

package com.example.kotmod6.presentation

import com.example.kotmod6.domain.model.Laureate
import com.example.kotmod6.domain.model.NobelPrize

enum class AppScreen {
    Login,
    List,
    Detail
}

enum class PrizeCategory(
    val title: String,
    val apiCode: String
) {
    All("Все категории", ""),
    Physics("physics", "physics"),
    Chemistry("chemistry", "chemistry"),
    Medicine("medicine", "medicine"),
    Literature("literature", "literature"),
    Peace("peace", "peace"),
    Economics("economics", "economics");

    companion object {
        val Values = entries
    }
}

data class LaureateEntry(
    val prize: NobelPrize,
    val laureate: Laureate
) {
    val key: String = "${prize.id}_${laureate.id}"
    val year: String = prize.year.toString()
    val category: String = prize.category
    val shortMotivation: String =
        if (laureate.motivation.length > 110) laureate.motivation.take(110).trimEnd() + "..." else laureate.motivation
}

data class NobelClientState(
    val checkingToken: Boolean = true,
    val screen: AppScreen = AppScreen.Login,
    val username: String = "student",
    val password: String = "studentpass",
    val filterYear: String = "",
    val selectedCategory: PrizeCategory = PrizeCategory.All,
    val loading: Boolean = false,
    val error: String? = null,
    val prizes: List<NobelPrize> = emptyList(),
    val selectedLaureate: LaureateEntry? = null
) {
    val visibleLaureates: List<LaureateEntry>
        get() = prizes
            .filter { prize ->
                val yearOk = filterYear.isBlank() || prize.year.toString().contains(filterYear)
                val categoryOk = selectedCategory.apiCode.isBlank() || prize.category == selectedCategory.apiCode
                yearOk && categoryOk
            }
            .flatMap { prize ->
                prize.laureates.map { laureate ->
                    LaureateEntry(prize = prize, laureate = laureate)
                }
            }
}

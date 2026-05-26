package com.example.kotmod6.domain.model

data class PrizeCategory(
    val apiCode: String,
    val title: String
) {
    companion object {
        val All = PrizeCategory("", "Все категории")

        val Values = listOf(
            All,
            PrizeCategory("phy", "Physics"),
            PrizeCategory("che", "Chemistry"),
            PrizeCategory("med", "Medicine"),
            PrizeCategory("lit", "Literature"),
            PrizeCategory("pea", "Peace"),
            PrizeCategory("eco", "Economics")
        )

        fun fromCode(code: String): PrizeCategory {
            return Values.firstOrNull { it.apiCode == code } ?: All
        }
    }
}

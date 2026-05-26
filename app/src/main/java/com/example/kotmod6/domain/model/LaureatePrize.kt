package com.example.kotmod6.domain.model

data class LaureatePrize(
    val laureateId: String,
    val fullName: String,
    val awardYear: String,
    val category: String,
    val categoryCode: String,
    val motivation: String,
    val sourceUrl: String?
) {
    val shortMotivation: String =
        if (motivation.length > 100) motivation.take(100).trimEnd() + "..." else motivation
}

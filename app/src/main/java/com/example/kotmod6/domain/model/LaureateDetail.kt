package com.example.kotmod6.domain.model

data class LaureateDetail(
    val id: String,
    val fullName: String,
    val awardYear: String,
    val category: String,
    val motivation: String,
    val birthPlace: String,
    val portraitUrl: String?,
    val wikipediaUrl: String?,
    val nobelPageUrl: String?
)

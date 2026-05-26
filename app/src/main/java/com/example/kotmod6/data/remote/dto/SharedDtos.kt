package com.example.kotmod6.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LocalizedTextDto(
    val en: String? = null
)

@Serializable
data class LinkDto(
    val rel: String? = null,
    val href: String? = null,
    val title: String? = null
)

@Serializable
data class LocationDto(
    val city: LocalizedTextDto? = null,
    val country: LocalizedTextDto? = null,
    val locationString: LocalizedTextDto? = null
)

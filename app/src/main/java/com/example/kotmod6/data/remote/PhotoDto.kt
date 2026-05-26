package com.example.kotmod6.data.remote

import com.squareup.moshi.Json

data class PhotoDto(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    @Json(name = "download_url")
    val downloadUrl: String
)

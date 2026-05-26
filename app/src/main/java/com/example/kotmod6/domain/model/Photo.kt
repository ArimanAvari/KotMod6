package com.example.kotmod6.domain.model

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val sourceUrl: String,
    val downloadUrl: String
) {
    val smallImageUrl: String = "https://picsum.photos/id/$id/520/360"
    val largeImageUrl: String = "https://picsum.photos/id/$id/1200/820"
    val sizeLabel: String = "$width × $height"
}

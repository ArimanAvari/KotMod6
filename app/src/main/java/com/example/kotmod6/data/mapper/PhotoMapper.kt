package com.example.kotmod6.data.mapper

import com.example.kotmod6.data.remote.PhotoDto
import com.example.kotmod6.domain.model.Photo

fun PhotoDto.toDomain(): Photo = Photo(
    id = id,
    author = author.ifBlank { "Unknown author" },
    width = width,
    height = height,
    sourceUrl = url,
    downloadUrl = downloadUrl
)

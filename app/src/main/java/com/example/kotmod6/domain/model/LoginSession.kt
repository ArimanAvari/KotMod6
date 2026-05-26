package com.example.kotmod6.domain.model

data class LoginSession(
    val token: String,
    val user: AppUser
)

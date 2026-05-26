package com.example.kotmod6.domain.model

data class AppUser(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String,
    val phone: String,
    val age: Int?,
    val city: String,
    val company: String
) {
    val fullName: String = "$firstName $lastName".trim()
}

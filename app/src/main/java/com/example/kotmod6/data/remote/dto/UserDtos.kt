package com.example.kotmod6.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsersResponseDto(
    val users: List<UserDto> = emptyList()
)

@Serializable
data class UserDto(
    val id: Int,
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val image: String = "",
    val phone: String = "",
    val age: Int? = null,
    val address: AddressDto? = null,
    val company: CompanyDto? = null
)

@Serializable
data class AddressDto(
    val city: String = "",
    val country: String = ""
)

@Serializable
data class CompanyDto(
    val name: String = "",
    val title: String = ""
)

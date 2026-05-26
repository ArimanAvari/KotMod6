package com.example.kotmod6.data.mapper

import com.example.kotmod6.data.remote.dto.LoginResponseDto
import com.example.kotmod6.data.remote.dto.UserDto
import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.domain.model.LoginSession

fun LoginResponseDto.toSession(): LoginSession {
    return LoginSession(
        token = accessToken ?: token.orEmpty(),
        user = AppUser(
            id = id,
            firstName = firstName,
            lastName = lastName,
            username = username,
            email = email,
            image = image,
            phone = "",
            age = null,
            city = "",
            company = ""
        )
    )
}

fun UserDto.toDomain(): AppUser {
    return AppUser(
        id = id,
        firstName = firstName,
        lastName = lastName,
        username = username,
        email = email,
        image = image,
        phone = phone,
        age = age,
        city = listOfNotNull(address?.city, address?.country)
            .filter { it.isNotBlank() }
            .joinToString(", "),
        company = listOfNotNull(company?.name, company?.title)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
    )
}

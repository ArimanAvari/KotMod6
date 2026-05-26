package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.domain.repository.AuthRepository

class GetUsersUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): List<AppUser> = repository.loadUsers()
}

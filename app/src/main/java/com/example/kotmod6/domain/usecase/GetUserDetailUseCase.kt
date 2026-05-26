package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.domain.repository.AuthRepository

class GetUserDetailUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(id: Int): AppUser = repository.loadUser(id)
}

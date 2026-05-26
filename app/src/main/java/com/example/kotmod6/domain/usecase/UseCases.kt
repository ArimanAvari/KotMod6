package com.example.kotmod6.domain.usecase

import com.example.kotmod6.domain.repository.NobelClientRepository

class LoginUseCase(private val repository: NobelClientRepository) {
    suspend operator fun invoke(username: String, password: String) = repository.login(username, password)
}

class GetPrizesUseCase(private val repository: NobelClientRepository) {
    suspend operator fun invoke() = repository.loadPrizes()
}

class GetPrizeDetailUseCase(private val repository: NobelClientRepository) {
    suspend operator fun invoke(year: Int, category: String) = repository.loadPrize(year, category)
}

class GetFavoritesUseCase(private val repository: NobelClientRepository) {
    suspend operator fun invoke() = repository.loadFavorites()
}

class AddFavoriteUseCase(private val repository: NobelClientRepository) {
    suspend operator fun invoke(year: Int, category: String) = repository.addFavorite(year, category)
}

class RemoveFavoriteUseCase(private val repository: NobelClientRepository) {
    suspend operator fun invoke(year: Int, category: String) = repository.removeFavorite(year, category)
}

class LogoutUseCase(private val repository: NobelClientRepository) {
    suspend operator fun invoke() = repository.logout()
}

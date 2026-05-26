package com.example.nobelapi.di

import com.example.nobelapi.data.repository.ExposedNobelRepository
import com.example.nobelapi.domain.usecase.FavoriteUseCases
import com.example.nobelapi.domain.usecase.GetLaureatesUseCase
import com.example.nobelapi.domain.usecase.GetPrizeDetailUseCase
import com.example.nobelapi.domain.usecase.GetPrizesUseCase
import com.example.nobelapi.domain.usecase.LoginUseCase
import com.example.nobelapi.presentation.AuthController
import com.example.nobelapi.presentation.FavoriteController
import com.example.nobelapi.presentation.PrizeController
import com.example.nobelapi.security.JwtConfig

class AppModule {
    val jwtConfig = JwtConfig()
    private val repository = ExposedNobelRepository()

    val authController = AuthController(LoginUseCase(repository, jwtConfig))
    val prizeController = PrizeController(
        getPrizesUseCase = GetPrizesUseCase(repository),
        getPrizeDetailUseCase = GetPrizeDetailUseCase(repository),
        getLaureatesUseCase = GetLaureatesUseCase(repository)
    )
    val favoriteController = FavoriteController(FavoriteUseCases(repository))
}

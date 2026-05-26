package com.example.nobelapi.di

import com.example.nobelapi.data.repository.InMemoryPrizeRepository
import com.example.nobelapi.domain.usecase.GetLaureatesUseCase
import com.example.nobelapi.domain.usecase.GetPrizeDetailUseCase
import com.example.nobelapi.domain.usecase.GetPrizesUseCase
import com.example.nobelapi.domain.usecase.LoginUseCase
import com.example.nobelapi.presentation.AuthController
import com.example.nobelapi.presentation.PrizeController
import com.example.nobelapi.security.JwtConfig

class AppModule {
    val jwtConfig = JwtConfig()

    private val prizeRepository = InMemoryPrizeRepository()
    private val loginUseCase = LoginUseCase(jwtConfig)
    private val getPrizesUseCase = GetPrizesUseCase(prizeRepository)
    private val getPrizeDetailUseCase = GetPrizeDetailUseCase(prizeRepository)
    private val getLaureatesUseCase = GetLaureatesUseCase(prizeRepository)

    val authController = AuthController(loginUseCase)
    val prizeController = PrizeController(
        getPrizesUseCase = getPrizesUseCase,
        getPrizeDetailUseCase = getPrizeDetailUseCase,
        getLaureatesUseCase = getLaureatesUseCase
    )
}

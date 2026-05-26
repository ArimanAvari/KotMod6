package com.example.kotmod6.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotmod6.data.local.TokenStorage
import com.example.kotmod6.data.remote.NetworkModule
import com.example.kotmod6.data.repository.ServerNobelRepository
import com.example.kotmod6.domain.model.NobelPrize
import com.example.kotmod6.domain.usecase.AddFavoriteUseCase
import com.example.kotmod6.domain.usecase.GetFavoritesUseCase
import com.example.kotmod6.domain.usecase.GetPrizeDetailUseCase
import com.example.kotmod6.domain.usecase.GetPrizesUseCase
import com.example.kotmod6.domain.usecase.LoginUseCase
import com.example.kotmod6.domain.usecase.LogoutUseCase
import com.example.kotmod6.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NobelClientViewModel(
    private val loginUseCase: LoginUseCase,
    private val getPrizesUseCase: GetPrizesUseCase,
    private val getPrizeDetailUseCase: GetPrizeDetailUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val logoutUseCase: LogoutUseCase,
    tokenStorage: TokenStorage
) : ViewModel() {

    private val _state = MutableStateFlow(NobelClientState())
    val state: StateFlow<NobelClientState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            tokenStorage.tokenFlow.collect { token ->
                if (token.isNullOrBlank()) {
                    _state.update { it.copy(checkingToken = false, screen = AppScreen.Login, loading = false) }
                } else if (_state.value.checkingToken || _state.value.screen == AppScreen.Login) {
                    _state.update { it.copy(checkingToken = false, screen = AppScreen.List) }
                    loadPrizes()
                    loadFavorites()
                }
            }
        }
    }

    fun changeUsername(value: String) = _state.update { it.copy(username = value, error = null) }
    fun changePassword(value: String) = _state.update { it.copy(password = value, error = null) }
    fun changeFilterYear(value: String) = _state.update { it.copy(filterYear = value.filter(Char::isDigit).take(4)) }
    fun changeFilterCategory(value: String) = _state.update { it.copy(filterCategory = value.trim()) }
    fun applyFilter() = _state.update { it.copy(message = "Фильтр применен") }

    fun login() {
        val current = _state.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _state.update { it.copy(error = "Введите логин и пароль") }
            return
        }

        viewModelScope.launch {
            runLoading {
                loginUseCase(current.username.trim(), current.password)
                _state.update { it.copy(password = "", screen = AppScreen.List, message = "Вход выполнен") }
                loadPrizes()
                loadFavorites()
            }
        }
    }

    fun loadPrizes() {
        viewModelScope.launch {
            runLoading {
                val prizes = getPrizesUseCase()
                _state.update { it.copy(prizes = prizes, screen = AppScreen.List) }
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = getFavoritesUseCase()
                _state.update { it.copy(favorites = favorites) }
            } catch (_: Exception) {
                // Если сервер пока недоступен, список премий все равно покажет свою ошибку.
            }
        }
    }

    fun openPrize(prize: NobelPrize) {
        viewModelScope.launch {
            runLoading {
                val detail = getPrizeDetailUseCase(prize.year, prize.category)
                _state.update { it.copy(selectedPrize = detail, screen = AppScreen.Detail) }
            }
        }
    }

    fun openFavorites() {
        viewModelScope.launch {
            runLoading {
                val favorites = getFavoritesUseCase()
                _state.update { it.copy(favorites = favorites, screen = AppScreen.Favorites) }
            }
        }
    }

    fun openList() = _state.update { it.copy(screen = AppScreen.List, selectedPrize = null) }
    fun backToList() = openList()

    fun addFavorite(prize: NobelPrize) {
        viewModelScope.launch {
            runLoading {
                val result = addFavoriteUseCase(prize.year, prize.category)
                val favorites = getFavoritesUseCase()
                _state.update { it.copy(message = result.message, favorites = favorites) }
            }
        }
    }

    fun removeFavorite(prize: NobelPrize) {
        viewModelScope.launch {
            runLoading {
                val result = removeFavoriteUseCase(prize.year, prize.category)
                val favorites = getFavoritesUseCase()
                _state.update {
                    it.copy(
                        message = result.message,
                        favorites = favorites
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.value = NobelClientState(checkingToken = false)
        }
    }

    private suspend fun runLoading(block: suspend () -> Unit) {
        _state.update { it.copy(loading = true, error = null, message = null) }
        try {
            block()
            _state.update { it.copy(loading = false) }
        } catch (error: Exception) {
            _state.update {
                it.copy(
                    loading = false,
                    error = error.localizedMessage?.takeIf(String::isNotBlank)
                        ?: "Не получилось выполнить запрос"
                )
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val storage = TokenStorage(context.applicationContext)
            val repository = ServerNobelRepository(NetworkModule.api, storage)

            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NobelClientViewModel(
                        loginUseCase = LoginUseCase(repository),
                        getPrizesUseCase = GetPrizesUseCase(repository),
                        getPrizeDetailUseCase = GetPrizeDetailUseCase(repository),
                        getFavoritesUseCase = GetFavoritesUseCase(repository),
                        addFavoriteUseCase = AddFavoriteUseCase(repository),
                        removeFavoriteUseCase = RemoveFavoriteUseCase(repository),
                        logoutUseCase = LogoutUseCase(repository),
                        tokenStorage = storage
                    ) as T
                }
            }
        }
    }
}

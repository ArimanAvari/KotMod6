package com.example.kotmod6.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kotmod6.data.local.TokenStorage
import com.example.kotmod6.data.remote.NetworkModule
import com.example.kotmod6.data.repository.DummyAuthRepository
import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.domain.usecase.GetUserDetailUseCase
import com.example.kotmod6.domain.usecase.GetUsersUseCase
import com.example.kotmod6.domain.usecase.LoginUseCase
import com.example.kotmod6.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val logoutUseCase: LogoutUseCase,
    tokenStorage: TokenStorage
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _usersState = MutableStateFlow<UsersState>(UsersState.Idle)
    val usersState: StateFlow<UsersState> = _usersState.asStateFlow()

    private val _detailState = MutableStateFlow<UserDetailState>(UserDetailState.Idle)
    val detailState: StateFlow<UserDetailState> = _detailState.asStateFlow()
    private var selectedUserId: Int? = null

    init {
        viewModelScope.launch {
            tokenStorage.tokenFlow.collect { token ->
                if (token.isNullOrBlank()) {
                    _authState.value = AuthState.LoggedOut
                    _usersState.value = UsersState.Idle
                    _detailState.value = UserDetailState.Idle
                } else {
                    _authState.value = AuthState.LoggedIn
                    if (_usersState.value == UsersState.Idle) {
                        loadUsers()
                    }
                }
            }
        }
    }

    fun changeUsername(value: String) {
        _loginState.update { it.copy(username = value, error = null) }
    }

    fun changePassword(value: String) {
        _loginState.update { it.copy(password = value, error = null) }
    }

    fun login() {
        val current = _loginState.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _loginState.update { it.copy(error = "Введите логин и пароль") }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(loading = true, error = null) }
            try {
                loginUseCase(username = current.username.trim(), password = current.password)
                _loginState.update { it.copy(loading = false, password = "") }
                loadUsers()
            } catch (error: Exception) {
                _loginState.update {
                    it.copy(loading = false, error = error.messageForUser("Неверные данные или нет соединения"))
                }
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UsersState.Loading
            _usersState.value = try {
                UsersState.Success(getUsersUseCase())
            } catch (error: Exception) {
                UsersState.Error(error.messageForUser("Не получилось загрузить пользователей"))
            }
        }
    }

    fun openUser(user: AppUser) {
        selectedUserId = user.id
        loadUserDetail(user.id)
    }

    fun retryUserDetail() {
        selectedUserId?.let { id ->
            loadUserDetail(id)
        }
    }

    private fun loadUserDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = UserDetailState.Loading
            _detailState.value = try {
                UserDetailState.Success(getUserDetailUseCase(id))
            } catch (error: Exception) {
                UserDetailState.Error(error.messageForUser("Не получилось загрузить пользователя"))
            }
        }
    }

    fun backToUsers() {
        _detailState.value = UserDetailState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _loginState.value = LoginState()
        }
    }

    private fun Throwable.messageForUser(fallback: String): String {
        return localizedMessage?.takeIf { it.isNotBlank() } ?: fallback
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val storage = TokenStorage(context.applicationContext)
            val repository = DummyAuthRepository(
                api = NetworkModule.dummyJsonApi,
                tokenStorage = storage
            )

            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AuthViewModel(
                        loginUseCase = LoginUseCase(repository),
                        getUsersUseCase = GetUsersUseCase(repository),
                        getUserDetailUseCase = GetUserDetailUseCase(repository),
                        logoutUseCase = LogoutUseCase(repository),
                        tokenStorage = storage
                    ) as T
                }
            }
        }
    }
}

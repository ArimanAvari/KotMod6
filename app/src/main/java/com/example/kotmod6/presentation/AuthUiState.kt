package com.example.kotmod6.presentation

import com.example.kotmod6.domain.model.AppUser

sealed interface AuthState {
    data object Checking : AuthState
    data object LoggedOut : AuthState
    data object LoggedIn : AuthState
}

data class LoginState(
    val username: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

sealed interface UsersState {
    data object Idle : UsersState
    data object Loading : UsersState
    data class Success(val users: List<AppUser>) : UsersState
    data class Error(val message: String) : UsersState
}

sealed interface UserDetailState {
    data object Idle : UserDetailState
    data object Loading : UserDetailState
    data class Success(val user: AppUser) : UserDetailState
    data class Error(val message: String) : UserDetailState
}

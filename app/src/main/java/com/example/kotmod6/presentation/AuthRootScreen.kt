package com.example.kotmod6.presentation

import androidx.compose.runtime.Composable
import com.example.kotmod6.domain.model.AppUser
import com.example.kotmod6.presentation.common.LoadingBlock
import com.example.kotmod6.presentation.detail.UserDetailScreen
import com.example.kotmod6.presentation.login.LoginScreen
import com.example.kotmod6.presentation.users.UsersScreen

@Composable
fun AuthRootScreen(
    authState: AuthState,
    loginState: LoginState,
    usersState: UsersState,
    detailState: UserDetailState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRetryLogin: () -> Unit,
    onRetryUsers: () -> Unit,
    onRetryDetail: () -> Unit,
    onUserClick: (AppUser) -> Unit,
    onBackToUsers: () -> Unit,
    onLogout: () -> Unit
) {
    when (authState) {
        AuthState.Checking -> LoadingBlock()
        AuthState.LoggedOut -> LoginScreen(
            state = loginState,
            onUsernameChange = onUsernameChange,
            onPasswordChange = onPasswordChange,
            onLogin = onLogin,
            onRetry = onRetryLogin
        )

        AuthState.LoggedIn -> when (detailState) {
            UserDetailState.Idle -> UsersScreen(
                state = usersState,
                onRetry = onRetryUsers,
                onUserClick = onUserClick,
                onLogout = onLogout
            )

            else -> UserDetailScreen(
                state = detailState,
                onBack = onBackToUsers,
                onRetry = onRetryDetail
            )
        }
    }
}

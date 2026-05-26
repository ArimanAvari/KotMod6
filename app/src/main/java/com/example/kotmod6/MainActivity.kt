package com.example.kotmod6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kotmod6.presentation.AuthViewModel
import com.example.kotmod6.presentation.AuthRootScreen
import com.example.kotmod6.ui.theme.KotMod6Theme

class MainActivity : ComponentActivity() {
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModel.factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KotMod6Theme {
                val authState by viewModel.authState.collectAsState()
                val loginState by viewModel.loginState.collectAsState()
                val usersState by viewModel.usersState.collectAsState()
                val detailState by viewModel.detailState.collectAsState()

                AuthRootScreen(
                    authState = authState,
                    loginState = loginState,
                    usersState = usersState,
                    detailState = detailState,
                    onUsernameChange = viewModel::changeUsername,
                    onPasswordChange = viewModel::changePassword,
                    onLogin = viewModel::login,
                    onRetryLogin = viewModel::login,
                    onRetryUsers = viewModel::loadUsers,
                    onRetryDetail = viewModel::retryUserDetail,
                    onUserClick = viewModel::openUser,
                    onBackToUsers = viewModel::backToUsers,
                    onLogout = viewModel::logout
                )
            }
        }
    }
}

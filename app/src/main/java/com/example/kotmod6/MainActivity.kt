package com.example.kotmod6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kotmod6.presentation.NobelClientRoot
import com.example.kotmod6.presentation.NobelClientViewModel
import com.example.kotmod6.ui.theme.KotMod6Theme

class MainActivity : ComponentActivity() {
    private val viewModel: NobelClientViewModel by viewModels {
        NobelClientViewModel.factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KotMod6Theme {
                val state by viewModel.state.collectAsState()

                NobelClientRoot(
                    state = state,
                    onUsernameChange = viewModel::changeUsername,
                    onPasswordChange = viewModel::changePassword,
                    onLogin = viewModel::login,
                    onRetry = viewModel::loadPrizes,
                    onFilterYearChange = viewModel::changeFilterYear,
                    onFilterCategoryChange = viewModel::changeFilterCategory,
                    onApplyFilter = viewModel::applyFilter,
                    onOpenLaureate = viewModel::openLaureate,
                    onBack = viewModel::backToList
                )
            }
        }
    }
}

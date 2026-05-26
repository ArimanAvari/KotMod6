package com.example.kotmod6

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kotmod6.presentation.HeartRateScreen
import com.example.kotmod6.presentation.HeartRateViewModel
import com.example.kotmod6.ui.theme.KotMod6Theme

class MainActivity : ComponentActivity() {
    private val viewModel: HeartRateViewModel by viewModels {
        HeartRateViewModel.factory(applicationContext)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        viewModel.onPermissionsResult(result.values.all { it })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KotMod6Theme {
                val state by viewModel.state.collectAsState()

                LaunchedEffect(Unit) {
                    permissionLauncher.launch(requiredPermissions())
                }

                HeartRateScreen(
                    state = state,
                    onRequestPermissions = {
                        permissionLauncher.launch(requiredPermissions())
                    },
                    onStartScan = viewModel::startScan,
                    onStopScan = viewModel::stopScan,
                    onConnect = viewModel::connect,
                    onDisconnect = viewModel::disconnect
                )
            }
        }
    }

    private fun requiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}

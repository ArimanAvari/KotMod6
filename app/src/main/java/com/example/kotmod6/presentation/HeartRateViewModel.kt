package com.example.kotmod6.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotmod6.data.ble.HeartRateBleManager

class HeartRateViewModel(
    private val manager: HeartRateBleManager
) : ViewModel() {
    val state = manager.state

    fun onPermissionsResult(granted: Boolean) = manager.setPermissionsGranted(granted)
    fun startScan() = manager.startScan()
    fun stopScan() = manager.stopScan()
    fun connect(address: String) = manager.connect(address)
    fun disconnect() = manager.disconnect()

    override fun onCleared() {
        manager.close()
        super.onCleared()
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HeartRateViewModel(HeartRateBleManager(context.applicationContext)) as T
                }
            }
        }
    }
}

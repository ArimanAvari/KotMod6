package com.example.kotmod6.data.ble

data class BleDeviceItem(
    val name: String,
    val address: String,
    val rssi: Int
)

data class HeartRateBleState(
    val permissionsGranted: Boolean = false,
    val bluetoothAvailable: Boolean = true,
    val scanning: Boolean = false,
    val status: String = "Готово",
    val devices: List<BleDeviceItem> = emptyList(),
    val connectedAddress: String? = null,
    val heartRate: Int? = null,
    val error: String? = null
)

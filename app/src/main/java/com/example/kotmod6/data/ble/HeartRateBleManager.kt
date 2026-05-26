package com.example.kotmod6.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class HeartRateBleManager(
    private val context: Context
) {
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        manager.adapter
    }

    private val _state = MutableStateFlow(HeartRateBleState(bluetoothAvailable = bluetoothAdapter != null))
    val state: StateFlow<HeartRateBleState> = _state.asStateFlow()

    private var bluetoothGatt: BluetoothGatt? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val item = BleDeviceItem(
                name = readDeviceName(device),
                address = device.address,
                rssi = result.rssi
            )

            _state.update { current ->
                val devices = current.devices
                    .filterNot { it.address == item.address }
                    .plus(item)
                    .sortedByDescending { it.rssi }

                current.copy(devices = devices, status = "Найдено устройств: ${devices.size}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            _state.update {
                it.copy(scanning = false, error = "Ошибка сканирования: $errorCode")
            }
        }
    }

    fun setPermissionsGranted(granted: Boolean) {
        _state.update {
            it.copy(
                permissionsGranted = granted,
                error = if (granted) null else "Разрешения Bluetooth не выданы"
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!hasScanPermission()) {
            setPermissionsGranted(false)
            return
        }

        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner == null) {
            _state.update { it.copy(bluetoothAvailable = false, error = "BLE недоступен") }
            return
        }

        _state.update {
            it.copy(
                scanning = true,
                status = "Идет сканирование",
                devices = emptyList(),
                error = null,
                heartRate = null
            )
        }
        scanner.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (hasScanPermission()) {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        }
        _state.update { it.copy(scanning = false, status = "Сканирование остановлено") }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        if (!hasConnectPermission()) {
            setPermissionsGranted(false)
            return
        }

        stopScan()
        disconnect()

        val device = runCatching { bluetoothAdapter?.getRemoteDevice(address) }.getOrNull()
        if (device == null) {
            _state.update { it.copy(error = "Устройство не найдено") }
            return
        }

        _state.update {
            it.copy(status = "Подключение к ${readDeviceName(device)}", connectedAddress = address, heartRate = null)
        }
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (hasConnectPermission()) {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
        }
        bluetoothGatt = null
        _state.update {
            it.copy(
                connectedAddress = null,
                heartRate = null,
                status = "Отключено"
            )
        }
    }

    fun close() {
        stopScan()
        disconnect()
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _state.update { it.copy(status = "Подключено. Поиск Heart Rate Service", error = null) }
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    gatt.close()
                    _state.update {
                        it.copy(
                            connectedAddress = null,
                            heartRate = null,
                            status = "Соединение закрыто"
                        )
                    }
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(HEART_RATE_SERVICE)
            val characteristic = service?.getCharacteristic(HEART_RATE_MEASUREMENT)

            if (characteristic == null) {
                _state.update {
                    it.copy(error = "Heart Rate Service не найден", status = "Нет нужного сервиса")
                }
                return
            }

            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic.getDescriptor(CLIENT_CONFIG)
            if (descriptor == null) {
                _state.update { it.copy(error = "CCC Descriptor не найден") }
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                @Suppress("DEPRECATION")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                gatt.writeDescriptor(descriptor)
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            _state.update {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    it.copy(status = "Уведомления включены. Heart Rate: -", error = null)
                } else {
                    it.copy(error = "Не удалось включить уведомления: $status")
                }
            }
        }

        @Deprecated("Used on Android 12 and lower")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            @Suppress("DEPRECATION")
            handleMeasurement(characteristic.value)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            handleMeasurement(value)
        }
    }

    private fun handleMeasurement(value: ByteArray) {
        val heartRate = parseHeartRate(value) ?: return
        _state.update {
            it.copy(heartRate = heartRate, status = "Данные получены", error = null)
        }
    }

    private fun parseHeartRate(value: ByteArray): Int? {
        if (value.size < 2) return null

        val flags = value[0].toInt()
        return if (flags and 0x01 == 0) {
            value[1].toInt() and 0xFF
        } else if (value.size >= 3) {
            (value[1].toInt() and 0xFF) or ((value[2].toInt() and 0xFF) shl 8)
        } else {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private fun readDeviceName(device: BluetoothDevice): String {
        return if (hasConnectPermission()) {
            device.name ?: "Unknown device"
        } else {
            "Unknown device"
        }
    }

    private fun hasScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasConnectPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        private val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}

package com.daffa0049.motocurity.viewModel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothViewModel:ViewModel() {
    private val _devices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val devices: StateFlow<Set<BluetoothDevice>> = _devices

    private var latitude: String? = null
    private var longitude: String? = null

    private val _id = MutableStateFlow("")
    val id = _id.asStateFlow()

    @Suppress("DEPRECATION")
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    if (!it.name.isNullOrBlank()) {
                        val currentSet = _devices.value
                        if (currentSet.none { d -> d.address == it.address }) {
                            _devices.value = currentSet + it
                        }
                    }
                }
            }
        }
    }

    fun updateId(id: String){
        _id.value = id
    }

    fun connectBluetooth(
        device: BluetoothDevice,
        onSuccess: (BluetoothSocket)->Unit,
        onFailure: (String)->Unit
    ){
        val myUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val uuid = device.uuids?.firstOrNull()?.uuid ?: myUuid
        val socket = device.createRfcommSocketToServiceRecord(uuid)
        @Suppress("DEPRECATION")
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket.connect() // blocking
                withContext(Dispatchers.Main) {
                    onSuccess(socket)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onFailure("Failed to connect to ${device.name} (${device.address}): ${e.message}")
                }
            }
        }
    }
    fun readFromSocket(
        socket: BluetoothSocket,
        output: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = socket.inputStream
                val buffer = ByteArray(1024)
                var bytesRead: Int

                while (socket.isConnected) {
                    bytesRead = inputStream.read(buffer)
                    if (bytesRead != -1) {
                        val data = String(buffer, 0, bytesRead)
                        output("data: $data")
                    } else {
                        output("Socket closed or data read error.")
                        break
                    }
                    delay(1000)// now valid because function is suspend
                }
            } catch (e: IOException) {
                e.printStackTrace()
                output("Error reading from socket: ${e.message}")
            }
        }
    }
    fun sendToBluetooth(socket: BluetoothSocket?, value: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (socket?.isConnected == true) {
                    socket.outputStream.write((value.toString() + "\n").toByteArray())
                    socket.outputStream.flush()
                    Log.d("Bluetooth", "Sent: $value")
                } else {
                    Log.e("Bluetooth", "Socket is not connected")
                }
            } catch (e: IOException) {
                Log.e("Bluetooth", "Error sending data: ${e.message}")
            }
        }
    }
    fun startScan(bluetoothAdapter: BluetoothAdapter) {
        _devices.value = emptySet()
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
    }

    fun stopScan(bluetoothAdapter: BluetoothAdapter) {
        bluetoothAdapter.cancelDiscovery()
    }

    fun updateBondedDevices(bluetoothAdapter: BluetoothAdapter?) {
        _devices.value = bluetoothAdapter?.bondedDevices?: emptySet()
    }

    fun processBluetoothData(raw: String): StateFlow<Pair<Double, Double>> {
        val latRegex = Regex("Lat:\\s*(-?\\d+\\.\\d+)")
        val lngRegex = Regex("Lng:\\s*(-?\\d+\\.\\d+)")

        latRegex.find(raw)?.let {
            latitude = it.groupValues[1]
        }

        lngRegex.find(raw)?.let {
            longitude = it.groupValues[1]
        }

        val latValue = latitude?.toDoubleOrNull()?:0.0
        val lonValue = longitude?.toDoubleOrNull()?:0.0

        return MutableStateFlow(latValue to lonValue)
    }
    fun getMovedMeter(raw: String): StateFlow<String>{
        var movedMeter = ""
        val movedMeterRemove = Regex("Jarak ke titik awal:\\s*(-?\\d+\\.\\d+)")
        movedMeterRemove.find(raw)?.let {
            movedMeter = it.groupValues[1]
        }
        return MutableStateFlow(movedMeter)
    }
}
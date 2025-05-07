package com.daffa0049.motocurity.viewModel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
class BluetoothViewModel:ViewModel() {
    private val _devices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val devices: StateFlow<Set<BluetoothDevice>> = _devices

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
    suspend fun readFromSocket(
        socket: BluetoothSocket,
        output: (String) -> Unit
    ) {
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
}
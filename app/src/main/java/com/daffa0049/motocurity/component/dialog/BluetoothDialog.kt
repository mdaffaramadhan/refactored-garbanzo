@file:Suppress("DEPRECATION")

package com.daffa0049.motocurity.component.dialog

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.viewModel.BluetoothViewModel

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDialog(
    bluetoothViewModel: BluetoothViewModel,
    onDismissReq: () -> Unit,
    onDeviceSelected: (BluetoothSocket) -> Unit
){
    val bondedDevices by bluetoothViewModel.devices.collectAsState()
    val context = LocalContext.current

    ObserveBluetooth(LocalContext.current, bluetoothViewModel)

    Dialog(onDismissRequest = onDismissReq) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .size(500.dp)
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxSize())
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bluetooth Devices",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            bluetoothViewModel.startScan(BluetoothAdapter.getDefaultAdapter())
                            bluetoothViewModel.updateBondedDevices(BluetoothAdapter.getDefaultAdapter())
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_refresh_24),
                            contentDescription = "Refresh"
                        )
                    }
                }

                if (bondedDevices.isEmpty()) {
                    Text(
                        text = "No paired devices found",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        items(bondedDevices.toList()) { device ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        bluetoothViewModel.connectBluetooth(
                                            device = device,
                                            onSuccess = {
                                                onDeviceSelected(it)
                                                Toast.makeText(context, "Successfully Connected", Toast.LENGTH_SHORT).show()
                                                },
                                            onFailure = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show()}
                                        )
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = device.name ?: "Unnamed Device")
                                    Text(
                                        text = device.address,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismissReq,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@SuppressLint("UnspecifiedRegisterReceiverFlag")
@Composable
fun ObserveBluetooth(context: Context, viewModel: BluetoothViewModel) {
    val intentFilter = remember { IntentFilter(BluetoothDevice.ACTION_FOUND) }
    DisposableEffect(Unit) {
        context.registerReceiver(viewModel.receiver, intentFilter)
        onDispose {
            context.unregisterReceiver(viewModel.receiver)
        }
    }
}
package com.daffa0049.motocurity.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass

class MotorViewModel:ViewModel() {
    val dataDummy = listOf(
        MotorDataClass(
            id = 1,
            nameMotor = "Vario 250",
            plateNum = "N 1562 XXX",
            battery = 100,
            picMotor = R.drawable.ic_launcher_background,
            trackCode = "123",
            isOn = true,
            isConnected = true
        ),
        MotorDataClass(
            id = 2,
            nameMotor = "Mio J 2012",
            plateNum = "N 3876 KSH",
            battery = 77,
            picMotor = R.drawable.ic_launcher_background,
            trackCode = "123",
            isOn = true,
            isConnected = true
        ),
        MotorDataClass(
            id = 3,
            nameMotor = "N-MAX",
            plateNum = "N 710 SJB",
            battery = 95,
            picMotor = R.drawable.ic_launcher_background,
            trackCode = "123",
            isOn = true,
            isConnected = true
        )
    )
    var selectedData by mutableStateOf<MotorDataClass>(
        MotorDataClass(
            0,
            "",
            "",
            0,
            0,
            "",
            isOn = false,
            isConnected = false
        )
    )
}
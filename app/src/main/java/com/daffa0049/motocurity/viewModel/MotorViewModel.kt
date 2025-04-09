package com.daffa0049.motocurity.viewModel

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
        )
    )
}
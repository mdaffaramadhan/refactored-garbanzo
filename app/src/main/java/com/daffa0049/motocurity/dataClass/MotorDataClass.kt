package com.daffa0049.motocurity.dataClass

import com.daffa0049.motocurity.R

data class MotorDataClass(
    val id: String = "",
    val nameMotor: String,
    val plateNum: String = "",
    val userUid: String? = "",
    val battery: Long = 100,
    val picMotor: Int = R.drawable.ic_launcher_background,
    val trackCode: String,
    val isOn: Boolean = true,
    val isConnected: Boolean = false,
    val distanceToActivate: Long = 10,
    val coordinate: String = ""
)

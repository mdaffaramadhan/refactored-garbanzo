package com.daffa0049.motocurity.dataClass

data class MotorDataClass(
    val id: Long,
    val nameMotor: String,
    val plateNum: String,
    val battery: Long,
    val picMotor: Int,
    val trackCode: String,
    val isOn: Boolean,
    val isConnected: Boolean,
    val distanceToActivate: Long = 10
)

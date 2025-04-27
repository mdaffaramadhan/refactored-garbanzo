package com.daffa0049.motocurity.dataClass

import com.daffa0049.motocurity.R
import com.google.firebase.database.Exclude

data class MotorDataClass(
    @Exclude var id: String = "",
    val nameMotor: String = "",
    val plateNum: String = "",
    val userUid: String? = "",
    val battery: Long = 100,
    val picMotor: Int = R.drawable.ic_launcher_background,
    val trackCode: String = "",
    var isOn: Boolean = true,
    var isConnected: Boolean = false,
    var distanceToActivate: Long = 10,
    val coordinate: String = ""
)

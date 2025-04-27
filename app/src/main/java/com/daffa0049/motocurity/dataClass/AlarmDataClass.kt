package com.daffa0049.motocurity.dataClass

data class AlarmDataClass(
    val id: String = "",
    val battery: Long = 100,
    val alarmIsOn: Boolean = true,
    val distanceToActivate: Long = 10,
    val isOn: Boolean = false,
)

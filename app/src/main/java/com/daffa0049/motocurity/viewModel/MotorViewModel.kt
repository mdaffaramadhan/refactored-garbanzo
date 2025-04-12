package com.daffa0049.motocurity.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class MotorViewModel:ViewModel() {
    private val _dataDummy = MutableStateFlow<List<MotorDataClass>>(
        listOf(
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
    )
    val dataDummy = _dataDummy.asStateFlow()
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

    fun addMotor(nameMotor: String, plateMotor: String, trackCode: String){
        _dataDummy.value += MotorDataClass(
            id = dataDummy.value.last().id+1,
            nameMotor = nameMotor,
            plateNum = plateMotor,
            trackCode = trackCode,
            battery = 100,
            picMotor = R.drawable.ic_launcher_background,
            isOn = true,
            isConnected = true
        )
    }
    fun editMotor(id: Long, nameMotor: String, plateMotor: String, trackCode: String){
        _dataDummy.value = _dataDummy.value.map {
            if(it.id == id) it.copy(
                nameMotor = nameMotor,
                plateNum = plateMotor,
                trackCode = trackCode
                ) else it
        }
    }
    fun deleteMotor(item: MotorDataClass){
        _dataDummy.value -= item
    }
    fun switchActionForIsOn(item: MotorDataClass){
        _dataDummy.value = _dataDummy.value.map {
            if(it.id == item.id) it.copy(isOn = !item.isOn) else it
        }
    }
}
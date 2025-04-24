package com.daffa0049.motocurity.viewModel

import androidx.lifecycle.ViewModel
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
    private val _selectedData = MutableStateFlow<MotorDataClass>(
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

    val selectedData = _selectedData.asStateFlow()

    fun addMotor(nameMotor: String, plateMotor: String, trackCode: String){
        _dataDummy.value += MotorDataClass(
            id = _dataDummy.value.size.toLong(),
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
    fun switchActionForList(item: MotorDataClass){
        _dataDummy.value = _dataDummy.value.map {
            if(it.id == item.id) it.copy(isOn = !item.isOn) else it
        }
    }

    fun selectData(item: MotorDataClass){
        _selectedData.value = item
    }

    fun switchActionForDetail(item: MotorDataClass){
        _selectedData.value = _selectedData.value.copy(isOn = !item.isOn)
        switchActionForList(item)
    }

    fun editDistanceToACtivate(id: Long, newDistance: Long){
        _dataDummy.value = _dataDummy.value.map {
            if(it.id == id){
                _selectedData.value = _selectedData.value.copy(distanceToActivate = newDistance)
                it.copy(distanceToActivate = newDistance)
            }
            else it
        }
    }
}
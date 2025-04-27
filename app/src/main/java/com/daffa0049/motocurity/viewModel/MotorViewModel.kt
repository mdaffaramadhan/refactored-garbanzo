package com.daffa0049.motocurity.viewModel

import androidx.lifecycle.ViewModel
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MotorViewModel:ViewModel() {
    private val _dataDummy = MutableStateFlow<List<MotorDataClass>>(
        listOf(
            MotorDataClass(
                id = "1",
                nameMotor = "Vario 250",
                plateNum = "N 1562 XXX",
                battery = 100,
                picMotor = R.drawable.ic_launcher_background,
                trackCode = "123",
                isOn = true,
                isConnected = true,
                userUid = "UtMFe95ENEYWnJvUvS6odEgKQEQ2"
            )
        )
    )
    val dataDummy = _dataDummy.asStateFlow()
//    private val _dataList = MutableStateFlow<List<MotorDataClass>>(
//        listOf(
//
//        )
//    )
//    val dataList = _dataList.asStateFlow()
    private val _selectedData = MutableStateFlow<MotorDataClass>(
        MotorDataClass(
            nameMotor = "",
            plateNum = "",
            isOn = false,
            isConnected = false,
            userUid = "",
            trackCode = ""
        )
    )

    val selectedData = _selectedData.asStateFlow()

    fun addMotor(nameMotor: String, plateMotor: String, trackCode: String){
        _dataDummy.value += MotorDataClass(
            id = _dataDummy.value.size.toString(),
            nameMotor = nameMotor,
            plateNum = plateMotor,
            trackCode = trackCode
        )
    }
    fun editMotor(id: String, nameMotor: String, plateMotor: String, trackCode: String){
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

    fun editDistanceToACtivate(id: String, newDistance: Long){
        _dataDummy.value = _dataDummy.value.map {
            if(it.id == id){
                _selectedData.value = _selectedData.value.copy(distanceToActivate = newDistance)
                it.copy(distanceToActivate = newDistance)
            }
            else it
        }
    }
    fun createMotor(
        nameMotor: String,
        plateMotor: String,
        trackCode: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ){
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val firebase = FirebaseFirestore.getInstance()
        val motor = MotorDataClass(
            nameMotor = nameMotor,
            plateNum = plateMotor,
            userUid = currentUserUid,
            trackCode = trackCode
        )
        firebase.collection("motocurity").document("motors")
            .collection("items")
            .add(motor)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {task->
                onError(task.message)
            }
    }

}
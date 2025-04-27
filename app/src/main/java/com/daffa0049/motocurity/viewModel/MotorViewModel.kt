package com.daffa0049.motocurity.viewModel

import androidx.lifecycle.ViewModel
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MotorViewModel:ViewModel() {
    private val _dataDummy = MutableStateFlow(
        emptyList<MotorDataClass>()
    )
    val dataDummy = _dataDummy.asStateFlow()
//    private val _dataList = MutableStateFlow<List<MotorDataClass>>(
//        listOf(
//
//        )
//    )
//    val dataList = _dataList.asStateFlow()
    private val _selectedData = MutableStateFlow(
        MotorDataClass()
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
    fun editMotor(
        id: String,
        nameMotor: String,
        plateMotor: String,
        trackCode: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ){
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("motocurity").document("motors")
            .collection("items")
            .document(id)
            .update(
                mapOf(
                    "nameMotor" to nameMotor,
                    "plateNum" to plateMotor,
                    "trackCode" to trackCode
                )
            ).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {e->
                onError(e.message)
            }
//        _dataDummy.value = _dataDummy.value.map {
//            if(it.id == id) it.copy(
//                nameMotor = nameMotor,
//                plateNum = plateMotor,
//                trackCode = trackCode
//                ) else it
//        }
    }
    fun deleteMotor(item: MotorDataClass){
        _dataDummy.value -= item
    }
    fun switchActionForList(item: MotorDataClass){
        FirebaseFirestore.getInstance()
            .collection("motocurity")
            .document("motors")
            .collection("items")
            .document(item.id)
            .update(
                mapOf(
                    "on" to !item.isOn
                )
            ).addOnCompleteListener {
                _dataDummy.value = _dataDummy.value.map {
                    if(it.id == item.id) it.copy(isOn = !item.isOn) else it
                }
            }
    }


    fun switchActionForDetail(item: MotorDataClass){
        FirebaseFirestore.getInstance()
            .collection("motocurity")
            .document("motors")
            .collection("items")
            .document(item.id)
            .update(
                mapOf(
                    "on" to !item.isOn
                )
            ).addOnCompleteListener {
                _selectedData.value = _selectedData.value.copy(isOn = !item.isOn)
                switchActionForList(item)
            }
    }

    fun editDistanceToACtivate(id: String, newDistance: Long){
        FirebaseFirestore.getInstance()
            .collection("motocurity")
            .document("motors")
            .collection("items")
            .document(id)
            .update(
                mapOf(
                    "distanceToActivate" to newDistance
                )
            ).addOnCompleteListener {
                _dataDummy.value = _dataDummy.value.map {
                    if(it.id == id){
                        _selectedData.value = _selectedData.value.copy(distanceToActivate = newDistance)
                        it.copy(distanceToActivate = newDistance)
                    }
                    else it
                }
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

    fun getMotor(onError: (String?) -> Unit){
        _dataDummy.value = emptyList()
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid?:return
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("motocurity").document("motors")
            .collection("items")
            .whereEqualTo("userUid", currentUserUid)
            .get()
            .addOnSuccessListener { querySnapshot->
                _dataDummy.value += querySnapshot.documents.map {document->
                    MotorDataClass(
                        id = document.id,
                        nameMotor = document.getString("nameMotor")?:"",
                        trackCode = document.getString("trackCode")?:"",
                        plateNum = document.getString("plateNum")?:"",
                        userUid = document.getString("userUid")?:"",
                        battery = document.getLong("battery")?:0,
                        picMotor = document.getLong("picMotor")?.toInt()?:R.drawable.ic_launcher_background,
                        isOn = document.getBoolean("on")?:false,
                        isConnected = document.getBoolean("connected")?:false,
                        distanceToActivate = document.getLong("distanceToActivate")?:10,
                        coordinate = document.getString("coordinate")?:""
                    )
                }
            }.addOnFailureListener {e->
                onError(e.message)
            }
    }

    fun getMotorById(id: String){
        _selectedData.value = MotorDataClass()
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("motocurity")
            .document("motors")
            .collection("items")
            .document(id)
            .get()
            .addOnSuccessListener {document->
                document.toObject(MotorDataClass::class.java)?.apply {
                    this.id = document.id
                    isOn = document.getBoolean("on") ?: false
                    isConnected = document.getBoolean("connected") ?: false
                    distanceToActivate = document.getLong("distanceToActivate") ?: 10
                    _selectedData.value = this
                }
            }

    }
    fun selectData(item: MotorDataClass){
        _selectedData.value = item
    }
}

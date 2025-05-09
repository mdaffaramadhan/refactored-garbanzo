package com.daffa0049.motocurity.viewModel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.daffa0049.motocurity.R
import com.daffa0049.motocurity.dataClass.MotorDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

class MotorViewModel:ViewModel() {
    private val _dataDummy = MutableStateFlow(
        emptyList<MotorDataClass>()
    )
    val dataDummy = _dataDummy.asStateFlow()

    private val _selectedData = MutableStateFlow(
        MotorDataClass()
    )
    val selectedData = _selectedData.asStateFlow()

    private val _showNotification = MutableStateFlow(false)
    val showNotification: StateFlow<Boolean> = _showNotification

    fun updateLocation(
        latitude: Double,
        longitude: Double,
        thresholdMeters: Float,
        lastLat: Double,
        lastLon: Double) {
        Log.d("LocationCheck", "Current: ($latitude, $longitude), Last: ($lastLat, $lastLon)")
        if (areCoordinatesEqual(lastLat, latitude) && areCoordinatesEqual(lastLon, longitude)) {
            Log.d("LocationCheck", "Coordinates are effectively equal. Skipping update.")
            return
        }
        if(lastLat == 0.0 || lastLon==0.0){
            Log.d("LocationCheck", "Last Coordinates are 0.")
            return
        }
        if(latitude == 0.0 || longitude==0.0){
            Log.d("LocationCheck", "Coordinates are 0.")
            return
        }

        val results = FloatArray(1)
        Location.distanceBetween(lastLat, lastLon, latitude, longitude, results)
        val distance = results[0]

        Log.d("LocationCheck", "Calculated distance: $distance meters, Threshold: $thresholdMeters")

        if (distance > thresholdMeters) {
            _showNotification.value = true
            Log.d("LocationCheck", "Notification triggered!")
        } else {
            Log.d("LocationCheck", "No notification needed.")
        }
    }

    private fun areCoordinatesEqual(a: Double, b: Double, epsilon: Double = 0.00001): Boolean {
        return kotlin.math.abs(a - b) < epsilon
    }

    fun resetNotification() {
        _showNotification.value = false
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
                    if(it.id == item.id){
                        it.copy(isOn = !item.isOn)
                    }else{
                        it
                    }
                }
            }
    }


    fun switchActionForDetail(item: MotorDataClass, latitude: Double, longitude: Double){

        FirebaseFirestore.getInstance()
            .collection("motocurity")
            .document("motors")
            .collection("items")
            .document(item.id)
            .update(
                mapOf(
                    "on" to !item.isOn,
                    "lastLat" to latitude,
                    "lastLon" to longitude,
                )
            ).addOnCompleteListener {
                _selectedData.value = _selectedData.value.copy(
                    isOn = !item.isOn,
                    lastLat = latitude?: 0.0,
                    lastLon = longitude?: 0.0
                )
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
                        coordinate = document.getString("coordinate")?:"",
                        lastLat = document.getDouble("lastLat")?: 0.0,
                        lastLon = document.getDouble("lastLon")?: 0.0
                    )
                }
            }.addOnFailureListener {e->
                onError(e.message)
            }
    }

    fun getCoordinate(id: String): Flow<Array<String>> = callbackFlow {
        val firebase = FirebaseFirestore.getInstance()
        val docRef = firebase.collection("motocurity")
            .document("motors")
            .collection("items")
            .document(id)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(arrayOf("", ""))
                close(error)
                return@addSnapshotListener
            }

            val coordinate = snapshot?.getString("coordinate") ?: ""
            val normalized = coordinate
                .replace(Regex("[\u2010-\u2015\u2212\uFE63\uFF0D]"), "-") // Replace various dashes with ASCII '-'
                .filter { it.code in 32..126 || it == '\n' } // Keep ASCII and newline
            val lines = normalized.lines()

            // Extract latitude and longitude
            val latitude = lines.find { it.contains("Latitude:") }
                ?.substringAfter("Latitude:")
                ?.trim()
                ?.toDoubleOrNull()

            val longitude = lines.find { it.contains("Longitude:") }
                ?.substringAfter("Longitude:")
                ?.trim()
                ?.toDoubleOrNull()

            trySend(arrayOf(latitude.toString(), longitude.toString()))

        }

        awaitClose { listener.remove() } // Removes the listener when the flow is closed
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
                    lastLat = document.getDouble("lastLat")?: 0.0
                    lastLon = document.getDouble("lastLon")?: 0.0
                    _selectedData.value = this
                }
            }
            .addOnFailureListener {
                // Handle failure, e.g., by setting an error state
                Log.e("MotorData", "Failed to fetch data", it)
            }
    }
    fun deleteMotor(
        id: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ){
        val firebase = FirebaseFirestore.getInstance()
        firebase.collection("motocurity")
            .document("motors")
            .collection("items")
            .document(id)
            .delete()
            .addOnCompleteListener {
                onSuccess()
            }
            .addOnFailureListener {e->
                onError(e.message)
            }
    }
}

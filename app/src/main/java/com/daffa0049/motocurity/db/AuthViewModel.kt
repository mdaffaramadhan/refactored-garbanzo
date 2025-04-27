package com.daffa0049.motocurity.db

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel:ViewModel() {
    fun registerUser(
        email: String,
        password: String,
        username: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ){
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    updateUsername(username)
                    onSuccess(uid)
                }
                else{
                    onError("Register Is Failed, ${task.exception?.message}")
                }
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
        ){
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    val uid = auth.currentUser?.uid?: return@addOnCompleteListener
                    onSuccess(uid)
                }
                else{
                    onError("Login Failed, ${task.exception?.message}")
                }
            }
    }

    fun updateUsername(
        username: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ){
        val auth = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()

        auth?.updateProfile(profileUpdates)?.addOnCompleteListener {
            onSuccess()
        }?.addOnFailureListener {e->
            onError(e.message.toString())
        }
    }


    fun logOut(){
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }
}

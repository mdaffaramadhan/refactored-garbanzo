package com.daffa0049.motocurity.db

import com.daffa0049.motocurity.dataClass.UserDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Auth {
    fun registerUser(
        email: String,
        password: String,
        username: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ){
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val user = UserDataClass(
                        id = uid,
                        email = email,
                        username = username
                    )
                    firestore.collection("users").document(uid)
                        .set(user)
                        .addOnCompleteListener {
                            onSuccess(uid)
                        }
                        .addOnFailureListener { e->
                            onError("Register Is Failed, ${e.message}")
                        }
                }
                else{
                    onError("Register Is Failed, ${task.exception?.message}")
                }
            }
    }
}
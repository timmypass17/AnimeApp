package com.example.firebasesample.ui.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firebasesample.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private const val TAG = "ProfileViewModel"
class ProfileViewModel(): ViewModel() {

    var user: User by mutableStateOf(User()) // default user

    init {
//        getCurrentUser()
    }

    fun getCurrentUser() {
        val db = Firebase.firestore
        val docRef = db.document("users/${Firebase.auth.currentUser?.uid}")
        Log.d(TAG, "Doing firebase stuff")

        // Check if user already in firestore
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject<User>() ?: User()
                    Log.i(TAG, "Document Exists: $documentSnapshot")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Document not found: $e")
            }
    }
}
package com.example.firebasesample.ui.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firebasesample.data.models.AnimePosterNode
import com.example.firebasesample.data.models.AnimeReview
import com.example.firebasesample.data.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private const val TAG = "ProfileViewModel"
class ProfileViewModel(): ViewModel() {

    var user: User by mutableStateOf(User()) // fetch user data from firebase and put here
    var animeFavorites: MutableMap<String, AnimePosterNode> by mutableStateOf(mutableMapOf())
    var animeReviewed: MutableMap<String, AnimeReview> by mutableStateOf(mutableMapOf())

    fun getCurrentUser() {
        // Check if user already in firestore
        Log.i(TAG, "getting user data")
        val db = Firebase.firestore
        val docRef = db.document("users/${Firebase.auth.currentUser?.uid}")
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject<User>() ?: User()
                    animeFavorites = user.animeFavorites
                    animeReviewed = user.animeReviews
                    Log.i(TAG, "Document Exists: $documentSnapshot")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Document not found: $e")
            }
    }

    fun isFavorited(animeId: String) = user.animeFavorites.containsKey(animeId)

}
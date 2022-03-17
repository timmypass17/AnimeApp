package com.example.firebasesample.ui.details.anime

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasesample.data.models.*
import com.example.firebasesample.data.network.KitsuApi
import com.example.firebasesample.ui.overview.KitsuApiStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

enum class KitsuApiStatus { LOADING, ERROR, DONE }
//val dummyAnime: Anime = Anime("", "", AnimeAttributes("","", AnimePoster(""), AnimeCover(""), "", "", "", ""))
val TAG = "AnimeDetailsViewModel"
class AnimeDetailsViewModel() : ViewModel() {

    // If these states change, then composable recomposes too (same for remember)
    var anime: Anime by mutableStateOf(Anime())
    var isFavorited: Boolean by mutableStateOf( false)
    var status: KitsuApiStatus by mutableStateOf(KitsuApiStatus.DONE)

    fun getAnime(id: String) {
        status = KitsuApiStatus.LOADING
        try {
            viewModelScope.launch {
                anime = KitsuApi.retrofitService.getAnime(id).data
                checkForFavorited(anime.id) // check if anime is favorited
                status = KitsuApiStatus.DONE
            }
        } catch (e: Exception) {
            anime = Anime()
            status = KitsuApiStatus.ERROR
        }
    }

    fun addtoFavorites(anime: Anime) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val docRef = db.document("users/${user?.uid}") // get current user path

        // Get current favorites
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Update favorites
                val userDocument = documentSnapshot.toObject<User>()
                val currentFavorites = userDocument?.animeFavorites
                currentFavorites?.put(anime.id, anime)
                isFavorited = true
                docRef
                    .update("animeFavorites", currentFavorites)
                    .addOnSuccessListener { Log.i(TAG, "Added anime ${anime.attributes.canonicalTitle} to favorites!") }
                    .addOnFailureListener { Log.w(TAG, "Fail to add to favorites") }
            }
            .addOnFailureListener {  }
    }

    fun removeFromFavorites(animeId: String) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val docRef = db.document("users/${user?.uid}") // get current user path
        // Get current favorites
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Update favorites
                val userDocument = documentSnapshot.toObject<User>()
                val currentFavorites = userDocument?.animeFavorites
                currentFavorites?.remove(animeId)
                isFavorited = false
                docRef
                    .update("animeFavorites", currentFavorites)
                    .addOnSuccessListener { Log.i(TAG, "Removed anime $animeId from favorites") }
                    .addOnFailureListener { Log.w(TAG, "Fail to remove anime $animeId from favorites") }
            }
            .addOnFailureListener {  }
    }

    fun checkForFavorited(animeId: String) {
        Log.i("AnimeDetailsViewModel", "Checking for $animeId")
        val db = Firebase.firestore
        val docRef = db.document("users/${Firebase.auth.currentUser?.uid}")
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject<User>() ?: User()
                    val animeFavorites = user.animeFavorites
                    isFavorited = animeFavorites.containsKey(animeId)
                    Log.i("AnimeDetailsViewModel", "$animeId is $isFavorited")
                }
            }
            .addOnFailureListener { e -> }
    }
}
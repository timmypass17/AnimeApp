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
val dummyAnime: Anime = Anime("", "", AnimeAttributes("","", AnimePoster(""), AnimeCover(""), "", "", "", ""))
val TAG = "AnimeDetailsViewModel"
class AnimeDetailsViewModel() : ViewModel() {

    var anime: Anime by mutableStateOf(dummyAnime.copy())
    var status: KitsuApiStatus by mutableStateOf(KitsuApiStatus.DONE)

    fun getAnime(id: String) {
        Log.i(TAG, "Attempting to get anime!")
        status = KitsuApiStatus.LOADING
        try {
            viewModelScope.launch {
                anime = KitsuApi.retrofitService.getAnime(id).data
                Log.i(TAG, "Got anime! $anime")
                status = KitsuApiStatus.DONE
            }
        } catch (e: Exception) {
            anime = dummyAnime.copy()
            status = KitsuApiStatus.ERROR
            Log.i(TAG, "Did not get anime")
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
//                val animeToAdd = FireBaseAnime(
//                    name = anime.attributes.canonicalTitle,
//                    posterUrl = anime.attributes.posterImage.small
//                )
                currentFavorites?.put(anime.id, anime)

                docRef
                    .update("animeFavorites", currentFavorites)
                    .addOnSuccessListener { Log.i(TAG, "Updated anime favorites!") }
                    .addOnFailureListener { Log.w(TAG, "Fail to update anime favorites :(") }
            }
            .addOnFailureListener {  }

    }
}
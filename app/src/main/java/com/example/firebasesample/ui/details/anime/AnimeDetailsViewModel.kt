package com.example.firebasesample.ui.details.anime

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasesample.data.models.*
import com.example.firebasesample.data.network.MalApi
import com.example.firebasesample.data.network.MalApiStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

val TAG = "AnimeDetailsViewModel"
class AnimeDetailsViewModel() : ViewModel() {

    // If these states change, then composable recomposes too (same for remember)
    var anime: Anime by mutableStateOf(Anime())
    var isFavorited: Boolean by mutableStateOf( false)
    var isWatched: Boolean by mutableStateOf(false)
    var status: MalApiStatus by mutableStateOf(MalApiStatus.LOADING)
    var reviews: List<AnimeReview> by mutableStateOf(listOf())

    fun getAnime(id: String) {
        status = MalApiStatus.LOADING
        try {
            viewModelScope.launch {
                anime = MalApi.retrofitService.getAnime(
                    query = id,
                    fields = "id,title,main_picture,start_season,synopsis,num_episodes"
                )
                checkForFavoritedWatched(anime.id) // check if anime is favorited
            }
        } catch (e: Exception) {
            anime = Anime()
            status = MalApiStatus.ERROR
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
                val animeToAdd = AnimePosterNode(
                    node = AnimePoster(
                        id = anime.id,
                        title = anime.title,
                        main_picture = AnimePicture(anime.main_picture.medium),
                        num_episodes = anime.num_episodes,
                        start_season = AnimeSeason(year = anime.start_season.year, season = anime.start_season.season)
                    )
                )
                currentFavorites?.put(anime.id, animeToAdd)
                isFavorited = true
                docRef
                    .update("animeFavorites", currentFavorites)
                    .addOnSuccessListener { Log.i(TAG, "Added anime ${anime.title} to favorites!") }
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

    fun checkForFavoritedWatched(animeId: String) {
        Log.i("AnimeDetailsViewModel", "Checking for $animeId")
        val db = Firebase.firestore
        val docRef = db.document("users/${Firebase.auth.currentUser?.uid}")
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject<User>() ?: User()
                    val animeFavorites = user.animeFavorites
                    val animeWatched = user.animeWatched
                    isFavorited = animeFavorites.containsKey(animeId)
                    isWatched = animeWatched.containsKey(animeId)
                    Log.i("AnimeDetailsViewModel", "$animeId is $isFavorited")
                    status = MalApiStatus.DONE
                }
            }
            .addOnFailureListener { e -> }
    }

    fun addToWatched(anime: Anime) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val docRef = db.document("users/${user?.uid}") // get current user path

        // Get current favorites
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Update favorites
                val userDocument = documentSnapshot.toObject<User>()
                val currentWatched = userDocument?.animeWatched
                val animeToAdd = AnimePosterNode(
                    node = AnimePoster(
                        id = anime.id,
                        title = anime.title,
                        main_picture = AnimePicture(anime.main_picture.medium),
                        num_episodes = anime.num_episodes,
                        start_season = AnimeSeason(year = anime.start_season.year, season = anime.start_season.season)
                    )
                )
                currentWatched?.put(anime.id, animeToAdd)
                isWatched = true
                docRef
                    .update("animeWatched", currentWatched)
                    .addOnSuccessListener { Log.i(TAG, "Added anime ${anime.title} to watchlist!") }
                    .addOnFailureListener { Log.w(TAG, "Fail to add to watchlist") }
            }
            .addOnFailureListener {  }
    }

    fun removeFromWatched(animeId: String) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val docRef = db.document("users/${user?.uid}") // get current user path
        // Get current favorites
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Update favorites
                val userDocument = documentSnapshot.toObject<User>()
                val userWatchlist = userDocument?.animeWatched
                userWatchlist?.remove(animeId)
                isWatched = false
                docRef
                    .update("animeWatched", userWatchlist)
                    .addOnSuccessListener { Log.i(TAG, "Removed anime $animeId from watchlist") }
                    .addOnFailureListener { Log.w(TAG, "Fail to remove anime $animeId from watchlist") }
            }
            .addOnFailureListener {  }
    }

    fun addReview(review: String, rating: Int) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val docRef = db.document("users/${user?.uid}") // get current user path
        val reviewDocRef = db.document("reviews/${anime.id}")

        // Create review object to add to firebase
        val newReview = AnimeReview(
            review = review,
            rating = rating,
            author = user?.uid ?: "",
            createdAt = -1
        )
        // Add review to user's collection
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Update reviews
                val userDocument = documentSnapshot.toObject<User>()
                val userAnimeReview = userDocument?.animeReviews
                userAnimeReview?.put(anime.id, newReview)
                docRef
                    .update("animeReviews", userAnimeReview)
                    .addOnSuccessListener { }
                    .addOnFailureListener { }
            }
            .addOnFailureListener {  }

        // Add review to review's collection
        reviewDocRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Update review list (reviews for this anime exists in firebase)
                if (documentSnapshot.exists()) {
                    val reviewDocument = documentSnapshot.toObject<AnimeReviews>()
                    val updatedReviews = reviewDocument?.reviews
                    updatedReviews?.add(newReview)
                    reviewDocRef
                        .update("reviews", updatedReviews)
                } else { // add to firebase for first time
                    reviewDocRef
                        .set(AnimeReviews(mutableListOf(newReview)))
                }
            }
    }

    fun getUserRatings(animeID: String) {
        // Get user ratings from firebase and store in viewmodel
        val db = Firebase.firestore
        val docRef = db.document("reviews/$animeID")
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                Log.i("AnimeDetailsViewModel", "Getting user ratings")
                if (documentSnapshot.exists()) {
                    val userReviews = documentSnapshot.toObject<AnimeReview>() ?: AnimeReview()
                    reviews = listOf(userReviews)
                }
            }
            .addOnFailureListener { e -> }
    }
}
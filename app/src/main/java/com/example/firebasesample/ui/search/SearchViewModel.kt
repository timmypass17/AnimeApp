package com.example.firebasesample.ui.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.network.MalApi
import com.example.firebasesample.data.network.MalApiStatus
import com.example.firebasesample.ui.overview.POSTER_FIELDS
import kotlinx.coroutines.launch

const val TAG = "SearchViewModel"
class SearchViewModel() : ViewModel() {
    var animeSearchResults: List<Anime> by mutableStateOf(listOf())

    init {
        // Provide default search query here
        getAnimeByName("jojo")
    }

    fun getAnimeByName(query: String) {
        try {
            viewModelScope.launch {
                Log.i(TAG, "Fetching anime: $query")
                val animeResult = MalApi.retrofitService.getAnimeByName(
                    title = query,
                    limit = 50,
                    fields = "id,title,main_picture,start_season,synopsis,num_episodes,recommendations,start_date,end_date,genres"
                )
                // TODO: related_anime doesnt show up, even with correct fields
                Log.i(TAG, animeResult.data.toString())
                // Convert to list of Anime
                val tempAnimeData = mutableListOf<Anime>()
                for (node in animeResult.data) {
                    tempAnimeData.add(node.node)
                }
                animeSearchResults = tempAnimeData
                Log.i(TAG, "Animes: $animeSearchResults")
            }
        } catch (e: Exception) { }
    }

}
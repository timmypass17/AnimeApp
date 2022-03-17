package com.example.firebasesample.ui.overview

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.network.KitsuApi
import kotlinx.coroutines.launch

enum class KitsuApiStatus { LOADING, ERROR, DONE }

class OverviewViewModel() : ViewModel() {

    // [("Title", "One piece..")..]
    val defaultAnimeData: List<Pair<String, MutableList<Anime>>> =
        listOf(
            Pair("Trending Anime", mutableListOf()),
            Pair("Top Anime", mutableListOf())
        )
    var animeData: List<Pair<String, MutableList<Anime>>> by mutableStateOf(defaultAnimeData)
    var errorMessage: String by mutableStateOf("")
    var trending_status: KitsuApiStatus by mutableStateOf(KitsuApiStatus.DONE)
    var top_status: KitsuApiStatus by mutableStateOf(KitsuApiStatus.DONE)

    init {
        getTrendingAnimes()
        getTopAnimes()
    }

    fun getTrendingAnimes() {
        trending_status = KitsuApiStatus.LOADING
        try {
            viewModelScope.launch {
                animeData[0].second.addAll(KitsuApi.retrofitService.getTrendingAnimeData().data)
                trending_status = KitsuApiStatus.DONE
                Log.i("OverviewModel", "Got trending anime")
            }
        } catch (e: Exception) {
            animeData[0].second.addAll(mutableListOf())
            errorMessage = e.message.toString()
            trending_status = KitsuApiStatus.ERROR
            Log.i("OverviewModel", "Did not get trending anime")
        }
    }

    fun getTopAnimes() {
        top_status = KitsuApiStatus.LOADING
        try {
            viewModelScope.launch {
                animeData[1].second.addAll(KitsuApi.retrofitService.getPopularAnimeData().data)
                top_status = KitsuApiStatus.DONE
                Log.i("OverviewModel", "Got popular anime")
            }
        } catch (e: Exception) {
            animeData[1].second.addAll(mutableListOf())
            errorMessage = e.message.toString()
            top_status = KitsuApiStatus.ERROR
            Log.i("OverviewModel", "Did not get popular anime")
        }
    }
}
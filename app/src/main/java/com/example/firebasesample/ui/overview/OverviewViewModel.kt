package com.example.firebasesample.ui.overview

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasesample.data.models.AnimePosterNode
import com.example.firebasesample.data.network.MalApi
import com.example.firebasesample.data.network.MalApiStatus
import kotlinx.coroutines.launch

class OverviewViewModel() : ViewModel() {

    val defaultAnimeData: List<Pair<String, MutableList<AnimePosterNode>>> =
        listOf(
            Pair("Top Anime", mutableListOf())
        )
    var animeData: List<Pair<String, MutableList<AnimePosterNode>>> by mutableStateOf(defaultAnimeData)
    var errorMessage: String by mutableStateOf("")

    var top_status: MalApiStatus by mutableStateOf(MalApiStatus.DONE)

    init {
        getTopAnimes()
    }

    fun getTopAnimes() {
        top_status = MalApiStatus.LOADING
        try {
            viewModelScope.launch {
                animeData[0].second.addAll(MalApi.retrofitService.getTopRankingAnimeData(
                    ranking = "all",
                    limit = 10,
                    fields = "title,main_picture,num_episodes,start_season"
                ).data)
                top_status = MalApiStatus.DONE
                Log.i("OverviewModel", "Got popular anime")
            }
        } catch (e: Exception) {
            animeData[0].second.addAll(mutableListOf())
            errorMessage = e.message.toString()
            top_status = MalApiStatus.ERROR
            Log.i("OverviewModel", "Did not get popular anime")
        }
    }
}
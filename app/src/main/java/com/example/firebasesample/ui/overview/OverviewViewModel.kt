package com.example.firebasesample.ui.overview

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasesample.data.models.AnimePosterNode
import com.example.firebasesample.data.models.OverviewData
import com.example.firebasesample.data.models.OverviewDataList
import com.example.firebasesample.data.network.MalApi
import com.example.firebasesample.data.network.MalApiStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

const val TAG = "OverviewViewModel"
const val OVERVIEW_ID = "0CJogOu7MZIRQGGXrnlp"
const val POSTER_FIELDS = "title,main_picture,num_episodes,start_season"
class OverviewViewModel() : ViewModel() {

    var animeData: MutableList<Pair<String, List<AnimePosterNode>>> by mutableStateOf(mutableListOf())
    var overviewDataList: OverviewDataList by mutableStateOf(OverviewDataList())

    var overviewStatus: MalApiStatus by mutableStateOf(MalApiStatus.LOADING)
    var overviewCount: Int by mutableStateOf(0)

    init {
        Log.i(TAG, "Creating viewmodel")
        setupOverview()
    }

    fun setupOverview() {
        overviewStatus = MalApiStatus.LOADING

        val db = Firebase.firestore
        val docRef = db.document("overview/${OVERVIEW_ID}")
        Log.i(TAG, "Calling setupOverview()")
        docRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.i("OverviewViewModel", "Setting up overview")
                    overviewDataList = documentSnapshot.toObject<OverviewDataList>() ?: OverviewDataList()
                    Log.i(TAG, overviewDataList.data.size.toString())
                    // Start querying api for anime data
                    for (item in overviewDataList.data) {
                        getAnimeRowData(item)
                    }
                }
            }
    }

    fun getAnimeRowData(overviewData: OverviewData) {
        try {
            viewModelScope.launch {
                when (overviewData.type) {
                    "ranking" -> addingByRanking(overviewData)
                    "season" -> addingBySeason(overviewData)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun addingByRanking(overviewData: OverviewData) {
        try {
            viewModelScope.launch {
                Log.i("OverviewViewModel", "Adding top ranking animes")
                val animeResult = MalApi.retrofitService.getTopRankingAnimeData(
                    ranking = overviewData.ranking,
                    limit = 10,
                    fields = POSTER_FIELDS
                )
                animeData.add(Pair(overviewData.title, animeResult.data))
                overviewCount += 1
                Log.i(TAG, overviewCount.toString())
                // Seen all api calls, update overview screen
                if (overviewCount == overviewDataList.data.size) {
                    overviewStatus = MalApiStatus.DONE
                }
            }
        } catch (e: Exception) { }
    }

    fun addingBySeason(overviewData: OverviewData) {
        try {
            viewModelScope.launch {
                Log.i("OverviewViewModel", "Adding season animes")
                Log.i(TAG, overviewData.year)
                Log.i(TAG, overviewData.season)

                val animeResult = MalApi.retrofitService.getAnimeBySeasonYear(
                    year = overviewData.year,
                    season = overviewData.season,
                    limit = 10,
                    fields = POSTER_FIELDS
                )
                animeData.add(Pair(overviewData.title, animeResult.data))
                overviewCount += 1
                Log.i(TAG, overviewCount.toString())
                // Seen all api calls, update overview screen
                if (overviewCount == overviewDataList.data.size) {
                    overviewStatus = MalApiStatus.DONE
                }
            }
        } catch (e: Exception) { }
    }
}
package com.example.firebasesample.ui.overview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.network.KitsuApi
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import retrofit2.http.Url

enum class KitsuApiStatus { LOADING, ERROR, DONE }

class OverviewViewModel() : ViewModel() {

    var animeData: MutableMap<String, List<Anime>> by mutableStateOf(mutableMapOf())
    var errorMessage: String by mutableStateOf("")
    var status: KitsuApiStatus by mutableStateOf(KitsuApiStatus.DONE)

    init {
        animeData["trending"] = listOf()
        fetchTrendingAnimeData()
    }

    fun fetchTrendingAnimeData() {
        status = KitsuApiStatus.LOADING
        try {
            viewModelScope.launch {
                animeData["trending"] = KitsuApi.retrofitService.getTrendingAnimeData().data
                status = KitsuApiStatus.DONE
                Log.i("OverviewModel", "Got trending anime")
            }
        } catch (e: Exception) {
            animeData["trending"] = listOf()
            errorMessage = e.message.toString()
            status = KitsuApiStatus.ERROR
            Log.i("OverviewModel", "Did not get trending anime")

        }
    }

    suspend fun getBitmap(url: String, context: Context): Bitmap {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url) // demo link
            .build()
        val result = (loader.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }
}
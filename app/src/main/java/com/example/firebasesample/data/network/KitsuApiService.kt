package com.example.firebasesample.data.network

import com.example.firebasesample.data.models.AnimeResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL =
    "https://kitsu.io/api/edge/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface KitsuApiService {
    @GET("trending/anime")
    suspend fun getTrendingAnimeData(): AnimeResponse
}

/**
 * Instance of Retrofit API service
 */
object KitsuApi {
    val retrofitService : KitsuApiService by lazy {
        retrofit.create(KitsuApiService::class.java)
    }
}
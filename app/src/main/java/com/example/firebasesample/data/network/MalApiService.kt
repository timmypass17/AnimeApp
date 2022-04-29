package com.example.firebasesample.data.network

import com.example.firebasesample.BuildConfig
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.models.AnimePosterResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

enum class MalApiStatus { LOADING, ERROR, DONE }

private const val BASE_URL =
    "https://api.myanimelist.net/v2/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

// Must run project once before using Buildconfig.apikey to generate the key
interface MalApiService {
    @Headers("X-MAL-CLIENT-ID: ${BuildConfig.apiKey}")
    @GET("anime/ranking")
    suspend fun getTopRankingAnimeData(
        @Query("ranking_type") ranking: String,
        @Query("limit") limit: Int,
        @Query("fields") fields: String
    ): AnimePosterResponse

    @Headers("X-MAL-CLIENT-ID: ${BuildConfig.apiKey}")
    @GET("anime/{id}")
    suspend fun getAnime(
        @Path("id") query: String,
        @Query("fields") fields: String
    ): Anime

    @Headers("X-MAL-CLIENT-ID: ${BuildConfig.apiKey}")
    @GET("anime/season/{year}/{season}")
    suspend fun getAnimeBySeasonYear(
        @Path("year") year: String,
        @Path("season") season: String,
        @Query("limit") limit: Int,
        @Query("fields") fields: String
    ) : AnimePosterResponse
}

/**
 * Instance of Retrofit API service
 */
object MalApi {
    val retrofitService : MalApiService by lazy {
        retrofit.create(MalApiService::class.java)
    }
}
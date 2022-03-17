package com.example.firebasesample.data.models

import android.util.Log

data class AnimeResponse(
    val data: List<Anime>
)

data class Anime(
    val id: String,
    val type: String,
    val attributes: AnimeAttributes
) {
    constructor() : this (
        id = "",
        type = "",
        attributes = AnimeAttributes(
            canonicalTitle = "",
            synopsis = "",
            posterImage = AnimePoster(),
            coverImage = AnimeCover(),
            episodeCount = "",
            startDate = "",
            endDate = "",
            youtubeVideoId = ""
        )
    )
}

data class AnimeAttributes(
    val canonicalTitle: String,
    val synopsis: String,
    val posterImage: AnimePoster,
    val coverImage: AnimeCover,
    val episodeCount: String?,  // null if series is ongoing
    var startDate: String,
    var endDate: String?, // // null if series is ongoing
    val youtubeVideoId: String
) {
    constructor() : this (
        canonicalTitle = "",
        synopsis = "",
        posterImage = AnimePoster(""),
        coverImage = AnimeCover(""),
        episodeCount = "",
        startDate = "",
        endDate = "",
        youtubeVideoId = ""
    )
}

data class AnimePoster(
    val small: String
) {
    constructor() : this (
        small = ""
    )
}

data class AnimeCover(
    val small: String
) {
    constructor() : this (
        small = ""
    )
}

/** Single Anime Response **/
data class SingleAnimeResponse(
    val data: Anime
)
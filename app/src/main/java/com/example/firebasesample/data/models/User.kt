package com.example.firebasesample.data.models

// Firebase needs no arg constructor
data class User(
    val uid: String,
    val username: String,
    val profileImage: String,
    val animeFavorites: MutableMap<String, Anime>
) {
    constructor() : this (uid = "", username = "", profileImage = "", animeFavorites = mutableMapOf())
}

class FireBaseAnime(
    val name: String,
    val posterUrl: String,
) {
    constructor() : this (name = "", posterUrl = "")
}
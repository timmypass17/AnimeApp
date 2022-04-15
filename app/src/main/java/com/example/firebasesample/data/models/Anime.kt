package com.example.firebasesample.data.models

data class AnimePosterResponse(
    val data: List<AnimePosterNode>
)

data class AnimePosterNode(
    val node: AnimePoster
) {
    constructor() : this (
        node = AnimePoster()
            )
}

// AnimePoster objects contains only partial info to display an anime
data class AnimePoster(
    val id: String,
    val title: String,
    val main_picture: AnimePicture,
    val num_episodes: String?,
    val start_season: AnimeSeason
) {
    constructor() : this (
        id = "",
        title = "",
        main_picture = AnimePicture(),
        num_episodes = "",
        start_season = AnimeSeason()
            )
}

data class AnimePicture(
    val medium: String
) {
    constructor() : this (
        medium = ""
            )
}

data class AnimeSeason(
    val year: String,
    val season: String
) {
    constructor() : this (
        year = "",
        season = ""
    )
}

// Full anime object
data class Anime(
    val id: String,
    val title: String,
    val main_picture: AnimePicture,
    val start_season: AnimeSeason,
    val synopsis: String,
    val num_episodes: String?
) {
    constructor() : this (
        id = "",
        title = "",
        main_picture = AnimePicture(""),
        start_season = AnimeSeason("", ""),
        synopsis = "",
        num_episodes = ""
    )
}

/** User Review Object **/
data class AnimeReviews(
    val reviews: MutableList<AnimeReview>
) {
    constructor() : this (
        reviews = mutableListOf()
    )
}

data class AnimeReview(
    val authorData: AnimeReviewAuthor,
    val animeData: AnimePosterNode
) {
    constructor() : this (
        authorData = AnimeReviewAuthor(),
        animeData = AnimePosterNode()
    )
}

data class AnimeReviewAuthor(
    val review: String,
    val rating: Int,
    val author: String,
    val createdAt: String,
    val username: String,
    val profileImage: String
) {
    constructor() : this (
        review = "",
        rating = -1,
        author = "",
        createdAt = "",
        username = "",
        profileImage = ""
    )
}
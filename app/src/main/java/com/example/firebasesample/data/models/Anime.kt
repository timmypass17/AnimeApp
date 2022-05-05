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

data class AnimeSearchResponse(
    val data: List<AnimeSearchResponseNode>
) {
    constructor() : this (
        data = listOf()
            )
}

data class AnimeSearchResponseNode(
    val node: Anime
) {
    constructor() : this (
        node = Anime()
            )
}

// Full anime object
data class Anime(
    val id: String,
    val title: String,
    val main_picture: AnimePicture,
    val start_season: AnimeSeason = AnimeSeason(),
    val synopsis: String,
    val num_episodes: String,
    val related_anime: List<AnimeRelated> = listOf(),
    val recommendations: List<AnimeRecommendation> = listOf(),
    val start_date: String = "",
    val end_date: String?,
    val genres: List<AnimeGenre> = listOf()
) {
    constructor() : this (
        id = "",
        title = "",
        main_picture = AnimePicture(),
        start_season = AnimeSeason(),
        synopsis = "",
        num_episodes = "",
        related_anime = listOf(),
        recommendations = listOf(),
        start_date = "",
        end_date = "",
        genres = listOf()
    )
}

data class AnimeGenre(
    val name: String
) {
    constructor() : this (
        name = ""
            )
}

data class AnimeRecommendation(
    val node: AnimeRecommendationNode
) {
    constructor() : this (
        node = AnimeRecommendationNode()
            )
}

data class AnimeRecommendationNode(
    val id: String,
    val title: String,
    val main_picture: AnimePicture
) {
    constructor() : this (
        id = "",
        title = "",
        main_picture = AnimePicture()
            )
}

data class AnimeRelated(
    val node: RelatedAnimeNode,
    val relation_type_formatted: String
) {
    constructor() : this (
        node = RelatedAnimeNode(),
        relation_type_formatted = ""
            )
}

data class RelatedAnimeNode(
    val id: String,
    val title: String,
    val main_picture: AnimePicture
) {
    constructor() : this (
        id = "",
        title = "",
        main_picture = AnimePicture()
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
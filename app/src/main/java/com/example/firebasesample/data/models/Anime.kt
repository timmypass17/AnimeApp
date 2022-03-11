package com.example.firebasesample.data.models

data class AnimeResponse(
    val data: List<Anime>
)

data class Anime(
    val type: String,
    val attributes: AnimeAttributes
)

data class AnimeAttributes(
    val canonicalTitle: String,
    val synopsis: String,
    val posterImage: AnimePoster,
    val coverImage: AnimeCover,
    val episodeCount: String?,
    val startDate: String
) {
    companion object {
        fun getSeasonYear(startDate: String): String {
            val date_parts = startDate.split("-")
            val year = date_parts[0]
            val month = date_parts[1].toInt()

            val season =
                when(month) {
                    in 3..5 -> "Spring"
                    in 6..8 -> "Summer"
                    in 9..11 -> "Autumn"
                    else -> {
                        "Winter"
                    }
            }

            return "$season $year"
        }
    }
}

data class AnimePoster(
    val tiny: String
)

data class AnimeCover(
    val tiny: String
)

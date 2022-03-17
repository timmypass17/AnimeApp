package com.example.firebasesample.ui.details.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.utli.getMonthDayYear
import com.example.firebasesample.utli.getSeasonYear
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
@ExperimentalMaterialApi
fun AnimeDetailsBody(
    anime: Anime,
    onClickFavorite: (Anime) -> Unit,
    onClickBack: () -> Unit
) {
    val systemUiController = rememberSystemUiController()   // Status bar black
    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = Color.Black
        )
    }
    val scrollState = rememberScrollState()
    LazyColumn {
        items(listOf(anime)) { anime ->
            AnimeDetail(anime, onClickFavorite = onClickFavorite)
        }
    }
}

@Composable
fun AnimeDetail(anime: Anime, onClickFavorite: (Anime) -> Unit) {
    AsyncImage(
        model = anime.attributes.coverImage.small,
        contentDescription = null,
        modifier = Modifier
            .height(275.dp)
            .background(Color.LightGray),
        contentScale = ContentScale.FillHeight
    )

    Column(modifier = Modifier.padding(24.dp)
    ) {
        AnimeHeading(
            anime = anime,
            id = anime.id,
            title = anime.attributes.canonicalTitle,
            season = getSeasonYear(anime.attributes.startDate),
            releaseDate = getMonthDayYear(anime.attributes.startDate),
            endDate = if (anime.attributes.endDate != null) getMonthDayYear(anime.attributes.endDate!!) else "Ongoing",
            onClickFavorite = onClickFavorite
        )

        Spacer(modifier = Modifier.padding(16.dp))
        AnimeSynopsis(synopsis = anime.attributes.synopsis)
        // Use exoplayer
    }
}

@Composable
fun AnimeHeading(anime: Anime, id: String, title: String, season: String, releaseDate: String, endDate: String,
    onClickFavorite: (Anime) -> Unit) {
    Row {
        Column(modifier = Modifier.weight(3f)) {
            Text(title)
            Text(season)
            Row {
                Text(releaseDate)
                Text(" - ")
                Text(endDate)
            }
        }
        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.End
        ) {
            Icon(Icons.Default.Favorite,
                "Favorite",
                modifier = Modifier.padding(8.dp)
                    .clickable { onClickFavorite(anime) }
            )
        }
    }
}

@Composable
fun AnimeSynopsis(synopsis: String) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Text(
        text = synopsis,
        maxLines = if (isExpanded) Int.MAX_VALUE else 4,
        overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.padding(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    )
    {
        Text(
            text = if (!isExpanded) "Read more" else "Read less",
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAnime() {
//    AnimeHeading("12", "One Piece", "2021-12-05", "2021-12-05", "2022-02-13", {})
}
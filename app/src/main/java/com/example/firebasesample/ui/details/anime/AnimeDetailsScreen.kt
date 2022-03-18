package com.example.firebasesample.ui.details.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebasesample.R
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.network.MalApiStatus
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
@ExperimentalMaterialApi
fun AnimeDetailsBody(
    anime: Anime,
    isFavorited: Boolean,
    isWatched: Boolean,
    onClickFavorite: (Anime) -> Unit,
    onClickRemoveFavorite: (String) -> Unit,
    onClickWatched: (Anime) -> Unit,
    onClickRemoveWatched: (String) -> Unit,
    status: MalApiStatus,
    onClickBack: () -> Unit
) {
    val systemUiController = rememberSystemUiController()   // Status bar black
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Black
        )
    }
    LazyColumn {
            // One item
            items(listOf(anime)) { anime ->
                AnimeDetail(
                    anime = anime,
                    isFavorited = isFavorited,
                    isWatched = isWatched,
                    onClickFavorite = onClickFavorite,
                    onClickRemoveFavorite = onClickRemoveFavorite,
                    onClickWatched = onClickWatched,
                    onClickRemoveWatched = onClickRemoveWatched,
                    status = status
                )
            }
        }
}

@Composable
fun AnimeDetail(
    anime: Anime,
    isFavorited: Boolean,
    isWatched: Boolean,
    onClickFavorite: (Anime) -> Unit,
    onClickRemoveFavorite: (String) -> Unit,
    onClickWatched: (Anime) -> Unit,
    onClickRemoveWatched: (String) -> Unit,
    status: MalApiStatus
) {
    AsyncImage(
        model = anime.main_picture.medium,
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
            isFavorited = isFavorited,
            isWatched = isWatched,
            onClickFavorite = onClickFavorite,
            onClickRemoveFavorite = onClickRemoveFavorite,
            onClickWatched = onClickWatched,
            onClickRemoveWatched = onClickRemoveWatched
        )

        Spacer(modifier = Modifier.padding(16.dp))
        AnimeSynopsis(synopsis = anime.synopsis)
    }
}

@Composable
fun AnimeHeading(
    anime: Anime,
    isFavorited: Boolean,
    isWatched: Boolean,
    onClickFavorite: (Anime) -> Unit,
    onClickRemoveFavorite: (String) -> Unit,
    onClickWatched: (Anime) -> Unit,
    onClickRemoveWatched: (String) -> Unit,
) {
    Row {
        Column(modifier = Modifier.weight(3f)) {
            Text(anime.title)
            Text(anime.start_season.season)
            Row {
                Text("start date")
                Text(" - ")
                Text("end date")
            }
        }
        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.End
        ) {
            Icon(
                imageVector = if (isFavorited) { Icons.Default.Favorite} else { Icons.Default.FavoriteBorder },
                contentDescription = "Favorite",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        if (isFavorited) {
                            onClickRemoveFavorite(anime.id)
                        } else {
                            onClickFavorite(anime)
                        }
                    },
                tint = colorResource(R.color.favorite)
            )

            Icon(
                imageVector = if (isWatched) { Icons.Default.CheckCircle } else { Icons.Default.Check },
                contentDescription = "Watched",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        if (isWatched) {
                            onClickRemoveWatched(anime.id)
                        } else {
                            onClickWatched(anime)
                        }
                    }
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
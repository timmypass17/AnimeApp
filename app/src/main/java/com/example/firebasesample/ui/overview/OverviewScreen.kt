package com.example.firebasesample.ui.overview

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.firebasesample.R
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.models.AnimeAttributes
import com.example.firebasesample.utli.Constants
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.URL


data class AnimeRowData(
    val title: String,
    val data: List<Anime>
)

// Top app bar: https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#TopAppBar(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function1,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp)
@Composable
fun OverviewBody(
    onClickLogout: () -> Unit = {},
    animeData: MutableMap<String, List<Anime>>
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                title = {},
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
//                    Row(
//                        Modifier.weight(1f),
//                        horizontalArrangement = Arrangement.SpaceAround) {
//                        TextButton(onClick = { /*TODO*/ }) {
//                            Text(text = "Animes", color = MaterialTheme.colors.onSurface)
//                        }
//                        TextButton(onClick = { /*TODO*/ }) {
//                            Text(text = "Mangas", color = MaterialTheme.colors.onSurface)
//                        }
//                        TextButton(onClick = { /*TODO*/ }) {
//                            Text(text = "My List", color = MaterialTheme.colors.onSurface)
//                        }
//                    }

                    Box() {
                        // RowScope here, so these icons will be placed horizontally
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Localized description")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(onClick = onClickLogout) {
                                Text("Logout")
                            }
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)) {
                item(animeData) {
                    repeat(1) {
                        AnimeRow(modifier = Modifier.padding(innerPadding), title = "Trending Animes", animes = animeData["trending"]!!)
                    }
                }
            }
        }
    )
}

@Composable
fun AnimeRow(modifier: Modifier, title: String, animes: List<Anime>) {
    Column {
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = title, modifier = Modifier.padding(start = 16.dp)
            , fontSize = 20.sp, fontFamily = Constants.robotoFamily, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.padding(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
            ) {
            items(animes) { anime ->
                AnimeRowPoster(anime)
            }
        }

    }
}


@Composable
fun AnimeRowPoster(anime: Anime) {
    val context = LocalContext.current

    var rgb by rememberSaveable { mutableStateOf(ContextCompat.getColor(context, R.color.white)) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        coroutineScope.launch {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(anime.attributes.posterImage.tiny) // demo link
                .build()
            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
            Palette.from(bitmap).generate { palette ->
                palette?.vibrantSwatch?.rgb?.let { colorValue ->
                    rgb = colorValue
                    Log.i("OverviewScreen", "Got egb: $rgb")
                }
            }
        }
    }


    Column(modifier = Modifier.width(128.dp)) {
        Image(
            painter = rememberImagePainter(anime.attributes.posterImage.tiny),
            contentDescription = null,
            modifier = Modifier
                .height(180.dp)
                .width(128.dp)
//                .background(Color.Gray),
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = anime.attributes.canonicalTitle.uppercase(),
            fontSize = 12.sp, fontFamily = Constants.robotoFamily,
            fontWeight = FontWeight.Medium,
            color = Color(rgb)
        )
        Text(
            text =
                if (anime.attributes.episodeCount != null) {
                    "Episodes ${anime.attributes.episodeCount}"
                } else {
                    "Ongoing"
                },

            fontFamily = Constants.robotoFamily, fontWeight = FontWeight.Bold
        )
        Text(text = AnimeAttributes.getSeasonYear(anime.attributes.startDate), fontSize = 12.sp, fontFamily = Constants.robotoFamily, fontWeight = FontWeight.Medium)
    }
}

// Generate palette synchronously and return it


@Preview
@Composable
fun PreviewOverviewBody() {
    OverviewBody(animeData = mutableMapOf())
}

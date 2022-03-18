package com.example.firebasesample.ui.overview

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.firebasesample.R
import com.example.firebasesample.data.models.AnimePosterNode
import com.example.firebasesample.utli.Constants
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

// Top app bar: https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#TopAppBar(kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Function0,kotlin.Function1,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp)
@Composable
fun OverviewBody(
    animeData: List<Pair<String, MutableList<AnimePosterNode>>>,
    onClickAnime: (AnimePosterNode) -> Unit
    ) {

    val systemUiController = rememberSystemUiController()   // Status bar black

    val useDarkIcons = MaterialTheme.colors.isLight
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    var expanded by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White, // Color.Transparent, Color.White
                title = {},
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
//            BottomAppBar {
//                overviewTabRow()
//            }
        },
        content = { innerPadding ->
            LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)) {
                items(animeData) { anime ->
                    repeat(1){
                        AnimeRow(title = anime.first, animes = anime.second, onClickAnime = onClickAnime)
                    }
                }
            }
        }
    )
}

@Composable
fun AnimeRow(title: String, animes: MutableList<AnimePosterNode>, onClickAnime: (AnimePosterNode) -> Unit) {
    Spacer(modifier = Modifier.padding(8.dp))
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title,
                modifier = Modifier.padding(start = 16.dp),
                fontSize = 20.sp, fontFamily = Constants.robotoFamily, fontWeight = FontWeight.Normal)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
            ) {
            items(animes) { anime ->
                AnimeItem(anime, onClickAnime)
            }
        }
    }
}


@Composable
fun AnimeItem(anime: AnimePosterNode, onClickAnime: (AnimePosterNode) -> Unit) {
    // TODO: Move this into viewmodel
    val context = LocalContext.current
    var rgb by rememberSaveable { mutableStateOf(ContextCompat.getColor(context, R.color.black)) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        coroutineScope.launch {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(anime.node.main_picture.medium) // demo link
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
    
    Column(
        modifier = Modifier
            .width(140.dp)
            .fillMaxHeight()
            .clickable {
                onClickAnime(anime)
            }
            .padding(8.dp)

    ) {
        AnimePoster(anime.node.main_picture.medium)
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = anime.node.title,
            fontSize = 12.sp, fontFamily = Constants.robotoFamily,
            fontWeight = FontWeight.Normal,
            color = Color(rgb),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (anime.node.num_episodes != null) "Episodes ${anime.node.num_episodes}" else "Ongoing",
            fontFamily = Constants.robotoFamily, fontWeight = FontWeight.Normal
        )
        Text(text = anime.node.start_season.season, fontSize = 12.sp, fontFamily = Constants.robotoFamily, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun AnimePoster(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .height(175.dp)
            .width(250.dp),
        contentScale = ContentScale.Fit
    )
}
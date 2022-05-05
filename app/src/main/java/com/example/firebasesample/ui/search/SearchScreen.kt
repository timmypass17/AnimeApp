package com.example.firebasesample.ui.search

import android.util.Log
import android.widget.RatingBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.models.AnimeRelated
import com.example.firebasesample.ui.details.anime.AnimeSynopsis
import com.example.firebasesample.ui.overview.AnimePoster
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun SearchScreen(
    animeData: List<Anime>,
    onClickSearch: (String) -> Unit,
    onClickAnime: (Anime) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Search for Anime") }) },
        content = {
            SearchBody(animeData, onClickSearch, onClickAnime)
        }
    )
}

@Composable
fun SearchBody(animeData: List<Anime>, onClickSearch: (String) -> Unit, onClickAnime: (Anime) -> Unit) {
    Column(modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)) {
        var text by rememberSaveable { mutableStateOf("") }

        SearchBar(text = text, onTextChange = { text = it }, onClickSearch)
        AnimeSearchResult(animeData = animeData, onClickAnime = onClickAnime)
    }
}

@Composable
fun SearchBar(text: String, onTextChange: (String) -> Unit, onClickSearch: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            value = text,
            onValueChange = onTextChange,
            label = { Text("Search an Anime...") }
        )
        Button(onClick = { onClickSearch(text) }) {
            Icon(Icons.Default.Search,
                contentDescription = "Search button"
            )
        }
    }
}

@Composable
fun AnimeSearchResult(animeData: List<Anime>, onClickAnime: (Anime) -> Unit) {
    LazyColumn {
        items(animeData) { anime ->
            AnimeSearchItem(anime, onClickAnime)
            Divider()
        }
    }
}

@Composable
fun AnimeSearchItem(
    anime: Anime,
    onClickAnime: (Anime) -> Unit
) {
    Row(Modifier.clickable { onClickAnime(anime) }.padding(top = 16.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(anime.main_picture.medium)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .width(110.dp)
                .height(180.dp),
            contentScale = ContentScale.FillHeight
        )
        Spacer(Modifier.padding(6.dp))
        Column {
            Row {
                Text(anime.start_season.season.capitalize(), color = Color.Gray)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(anime.start_season.year, color = Color.Gray)
            }
            Text(
                text = anime.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            FlowRow(
                mainAxisSpacing = 6.dp,
                crossAxisSpacing = 4.dp
            ) {
                if (anime.genres.isNotEmpty()) {
                    Text(anime.genres[0].name, color = Color.Gray)
                    if (anime.genres.size >= 2) {
                        Text("|", color = Color.Gray)
                        Text(anime.genres[1].name, color = Color.Gray)
                    }
                }
            }
            AnimeSynopsis(synopsis = anime.synopsis)
        }
    }
}
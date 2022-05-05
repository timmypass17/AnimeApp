package com.example.firebasesample.ui.profile

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.firebasesample.data.models.*
import com.example.firebasesample.ui.details.anime.AnimeSynopsis
import com.example.firebasesample.ui.overview.AnimeItem
import com.example.firebasesample.ui.overview.AnimePoster
import com.example.firebasesample.ui.search.AnimeSearchItem
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileBody(
    user: User,
    animeFavorites: MutableMap<String, AnimePosterNode>,
    animeReviewed: MutableMap<String, AnimeReview>,
    onClickAnime: (AnimePosterNode) -> Unit,
    onClickLogout: () -> Unit,
    onClickBack: () -> Unit,
    onClickReview: (AnimeReview) -> Unit
    ) {
    Log.i("ProfileScreen", animeFavorites.toString())
    Scaffold(
        topBar = {
            var expanded by rememberSaveable { mutableStateOf(false) }
            TopAppBar(
                title = { Text(user.username) },
                navigationIcon = {
                    IconButton(onClick = { onClickBack() }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Back")
                    }
                },
                actions = {
                    // RowScope here, so these icons will be placed horizontally
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Localized description")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = { onClickLogout() }
                        ) {
                            Text("Logout")
                        }
                    }
                }
            )
        },
    ) {
        Column {
            Profile(user)
            ProfileTab(user = user, onClickAnime = onClickAnime, onClickReview = onClickReview)
        }
    }
}

@Composable
fun ProfileTab(user: User, onClickAnime: (AnimePosterNode) -> Unit, onClickReview: (AnimeReview) -> Unit) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Favorites", "Reviews", "Seen", "Currently Watching", "Saved")
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = Color.Transparent,
        edgePadding = 0.dp
    ) {
        tabs.forEachIndexed { tabIndex, tab ->
            Tab(
                selected = selectedTabIndex == tabIndex,
                onClick = { selectedTabIndex = tabIndex },
                text = { Text(text = tab) }
            )
        }
    }
    when (tabs[selectedTabIndex]) {
        "Favorites" -> Favorites(animeReviewed = user.animeFavorites, onClickAnime = onClickAnime)
        "Reviews" -> Reviews(animeReviewed = user.animeReviews, onClickReview = onClickReview)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Reviews(animeReviewed: MutableMap<String, AnimeReview>, onClickReview: (AnimeReview) -> Unit) {
    // Extract anime poster nodes from user's reviewed to display onto profile screen
    val reviews = mutableListOf<AnimeReview>()
    val reviewData = animeReviewed.values
    for (currentReview in reviewData) {
        reviews.add(currentReview)
    }
    Log.i("ProfileScreen", reviewData.toString())
    LazyColumn {
        items(reviews) { anime ->
            ReviewItem(anime, onClickReview)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Favorites(animeReviewed: MutableMap<String, AnimePosterNode>, onClickAnime: (AnimePosterNode) -> Unit) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
    ) {

        items(animeReviewed.values.toMutableList()) { anime ->
            AnimeItem(anime, onClickAnime)
        }
    }
}

@Composable
fun Profile(user: User) {
    Row(
        modifier = Modifier
            .padding(16.dp)
    ) {
        AsyncImage(
            model = user.profileImage,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(user.animeReviews.size.toString())
                Text("Reviews")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(user.animeFavorites.size.toString())
                Text("Favorites")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("0")
                Text("Followers")
            }
        }
    }
}

@Composable
fun ReviewItem(
    anime: AnimeReview,
    onClickReview: (AnimeReview) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().clickable { onClickReview(anime) }.padding(8.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(anime.animeData.node.main_picture.medium)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .width(55.dp)
                .height(90.dp)
                .background(Color.LightGray),
            contentScale = ContentScale.FillHeight
        )
        Spacer(Modifier.padding(6.dp))
        Column {
            Row {
                Text(anime.animeData.node.start_season.season.capitalize(), color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(anime.animeData.node.start_season.year, color = Color.Gray, fontSize = 12.sp)
            }
            Text(
                text = anime.animeData.node.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "\"${anime.authorData.review}\"",
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
        }
    }
    Divider()
}
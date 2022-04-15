package com.example.firebasesample.ui.profile

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.SuccessResult
import com.example.firebasesample.data.models.AnimePosterNode
import com.example.firebasesample.data.models.AnimeReview
import com.example.firebasesample.data.models.User
import com.example.firebasesample.ui.overview.AnimeItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileBody(
    user: User,
    animeFavorites: MutableMap<String, AnimePosterNode>,
    animeReviewed: MutableMap<String, AnimeReview>,
    onClickAnime: (AnimePosterNode) -> Unit,
    onClickLogout: () -> Unit,
    ) {
    Log.i("ProfileScreen", animeFavorites.toString())
    Scaffold(
        topBar = {
            var expanded by rememberSaveable { mutableStateOf(false) }
            TopAppBar(
                backgroundColor = Color.White,
                title = { Text(user.username) },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
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
            ProfileTabRow(user = user, onClickAnime = onClickAnime)
        }
    }
}

@Composable
fun ProfileTabRow(user: User, onClickAnime: (AnimePosterNode) -> Unit) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Reviews", "Favorites", "Seen", "Currently Watching", "Saved")
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
        "Reviews" -> Reviews(animeReviewed = user.animeReviews, onClickAnime = onClickAnime)
        "Favorites" -> Favorites(animeReviewed = user.animeFavorites, onClickAnime = onClickAnime)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Reviews(animeReviewed: MutableMap<String, AnimeReview>, onClickAnime: (AnimePosterNode) -> Unit) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
    ) {
        // Extract anime poster nodes from user's reviewed to display onto profile screen
        val reviews = mutableListOf<AnimePosterNode>()
        val reviewData = animeReviewed.values
        for (currentReview in reviewData) {
            reviews.add(currentReview.animeData)
        }

        items(reviews) { anime ->
            AnimeItem(anime, onClickAnime)
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
                Text("Seen")
            }
        }
    }
}
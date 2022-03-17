package com.example.firebasesample.ui.profile

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebasesample.data.models.Anime
import com.example.firebasesample.data.models.User
import com.example.firebasesample.ui.overview.AnimeItem
import com.example.firebasesample.ui.overview.AnimePoster

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileBody(
    getUserData: () -> Unit,
    user: User,
    onClickAnime: (Anime) -> Unit,
    onClickLogout: () -> Unit,
    ) {

    getUserData()
    Log.i("ProfileViewModel", "Getting user data from firebase")

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
            ProfileHeader(user = user)
            LazyVerticalGrid(
                cells = GridCells.Fixed(3),
            ) {
                items(user.animeFavorites.values.toMutableList()) { anime ->
                    AnimeItem(anime, onClickAnime)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(8.dp)
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
                .background(Color.Gray)
                .weight(1f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(user.animeFavorites.size.toString())
                Text("Reviews")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("0")
                Text("Followers")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("0")
                Text("Following")
            }
        }
    }
}
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.firebasesample.data.models.AnimePosterNode
import com.example.firebasesample.data.models.User
import com.example.firebasesample.ui.overview.AnimeItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileBody(
    user: User,
    animeFavorites: MutableMap<String, AnimePosterNode>,
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
            ProfileHeader(user = user)
            LazyVerticalGrid(
                cells = GridCells.Fixed(3),
            ) {
                items(animeFavorites.values.toMutableList()) { anime ->
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
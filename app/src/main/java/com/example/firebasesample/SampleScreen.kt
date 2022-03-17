package com.example.firebasesample

import android.media.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class SampleScreen(
    val icon: ImageVector
) {
    Login(Icons.Default.Face),
    SignUp(Icons.Default.Face),
    Overview(Icons.Default.Home),
    AnimeDetails(Icons.Default.Face),
    Search(Icons.Default.Search),
    Profile(Icons.Default.Person);

    companion object {
        fun fromRoute(route: String?): SampleScreen =
            when (route?.substringBefore("/")) {
                Login.name -> Login
                SignUp.name -> SignUp
                Overview.name -> Overview
                AnimeDetails.name -> AnimeDetails
                Search.name -> Search
                Profile.name -> Profile
                null -> Login
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}

enum class BottomSampleScreen(
    val icon: ImageVector
) {
    Home(
        icon = Icons.Filled.Home
    ),
    Search(
        icon = Icons.Filled.Search
    ),
    Profile(
        icon = Icons.Filled.Person
    );
}
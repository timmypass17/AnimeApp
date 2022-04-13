package com.example.firebasesample.ui.details.review

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.firebasesample.data.models.Anime

@Composable
fun ReviewBody(anime: Anime) {
    Text(text = anime.title)
}